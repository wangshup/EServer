package com.dd.server.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZkCore {

    public final static int WAIT_INIT_MAX = 30 * 1000;

    private static final Logger logger = LoggerFactory.getLogger(ZkCore.class);

    private CuratorFramework client;

    private Map<String, TreeCache> treeCacheMap = new ConcurrentHashMap<>();
    private Map<TreeCache, CountDownLatch> cacheInitState = new ConcurrentHashMap<>();

    public ZkCore(String addrs, String namespace) {
        client = CuratorFrameworkFactory.builder()
                .namespace(namespace)
                .connectString(addrs)
                .sessionTimeoutMs(10 * 1000)
                .connectionTimeoutMs(3 * 1000)
                .retryPolicy(new RetryNTimes(1024, 5000))
                .build();
        client.start();
    }

    public void close() {
        this.treeCacheMap.forEach((k, v) -> CloseableUtils.closeQuietly(v));
        CloseableUtils.closeQuietly(client);
    }

    //TODO 2019-03-03 14:40  Node mode

    public void addConnectionListener(ConnectionStateListener listener){
        client.getConnectionStateListenable().addListener(listener);
    }

    public String createNode(String path) throws Exception {
        return client.create()
                .creatingParentsIfNeeded()
                .forPath(path);
    }

    public String createNode(String path, String initData) throws Exception {
        return client.create()
                .creatingParentsIfNeeded()
                .forPath(path, initData.getBytes());
    }

    public String createNode(String path, byte[] initData) throws Exception {
        return client.create()
                     .creatingParentsIfNeeded()
                     .forPath(path, initData);
    }

    public String createNode(String path, String initData, CreateMode mode) throws Exception {
        return client.create().creatingParentsIfNeeded().withMode(mode).forPath(path, initData.getBytes());
    }

    public String createNode(String path, byte[] initData, CreateMode mode) throws Exception {
        return client.create().creatingParentsIfNeeded().withMode(mode).forPath(path, initData);
    }

    public Stat set(String path, String data) throws Exception {
        return client.setData().forPath(path, data.getBytes());
    }

    public Stat set(String path, byte[] data) throws Exception {
        return client.setData().forPath(path, data);
    }

    public byte[] get(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public String getAsString(String path) throws Exception {
        return new String(get(path));
    }

    public void addDataWatcher(String path, ZKListener zkListener) {
        addDataWatcher(path, zkListener, true);
    }

    public void addDataWatcher(String path, ZKListener zkListener, boolean waitInit) {
        TreeCache treeCache = buildTreeCache(path, waitInit);
        treeCache.getListenable().addListener(new TreeCacheCacheProxy(path, zkListener));
    }

    public List<String> getAllChildrenName(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public Map<String, ChildData> getChildDataCache(String path) {
        return getChildDataCache(path, true);
    }

    public Map<String, ChildData> getChildDataCache(String path, boolean waitInit) {
        TreeCache treeCache = buildTreeCache(path, waitInit);
        Map<String, ChildData> map = treeCache.getCurrentChildren(path);
        return map == null ? Collections.EMPTY_MAP : map;
    }

    public void remove(String path) throws Exception {
        client.delete().forPath(path);
    }

    public interface ZKListener {
        void onNodeAdded(String path, byte[] data);

        void onNodeUpdated(String path, byte[] data);

        void onNodeRemoved(String path, byte[] data);

        default void initeCacheFinish() {
            logger.info(" wait init finish");
        }
    }

    //TODO 2019-03-04 11:49 replace lower level cache

    private TreeCache buildTreeCache(String path, boolean waitInit) {
        for (String s : this.treeCacheMap.keySet()) {
            if (path.startsWith(s)) {
                TreeCache curCache = this.treeCacheMap.get(s);

                if (waitInit) {
                    CountDownLatch initState = cacheInitState.get(curCache);
                    try {
                        boolean fin = initState.await(WAIT_INIT_MAX, TimeUnit.MILLISECONDS);
                        if (!fin) {
                            throw new RuntimeException(" wait init zk cache time out " + path + " : " + WAIT_INIT_MAX);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return curCache;
            }
        }

        TreeCache treeCache = this.treeCacheMap.computeIfAbsent(path, p -> {
            TreeCache ret = new TreeCache(client, path);
            try {
                ret.start();

                CountDownLatch initStat = new CountDownLatch(1);
                cacheInitState.put(ret, initStat);

                TreeCacheListener listener = (client, event) -> {
                    switch (event.getType()) {
                        case INITIALIZED: {
                            initStat.countDown();
                            logger.info(" wait init finish {}", path);
                        }
                    }
                };

                ret.getListenable().addListener(listener);

                if (waitInit) {
                    boolean fin = initStat.await(WAIT_INIT_MAX, TimeUnit.MILLISECONDS);
                    if (!fin) {
                        throw new RuntimeException(" wait init zk cache time out " + path + " : " + WAIT_INIT_MAX);
                    }
                }

                return ret;
            } catch (Exception e) {
                logger.error(" start TreeCache fail , path {}", path, e);
                return null;
            }
        });

        logger.info(" new TreeCache :{}, cur = {} ", path, this.treeCacheMap.keySet());
        return treeCache;
    }

    class TreeCacheCacheProxy implements TreeCacheListener {

        ZKListener zkListener;
        String path;

        public TreeCacheCacheProxy(String path, ZKListener zkListener) {
            this.zkListener = zkListener;
            this.path = path;
        }

        @Override
        public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
            if ( event.getData() == null || !event.getData().getPath().startsWith(path)) {
                //logger.info(" skip event : " + ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
                //        + new String(event.getData().getData()));
                logger.warn(" skip event : {} ", event);
                return;
            }

            switch (event.getType()) {
                case NODE_ADDED: {
                    this.zkListener.onNodeAdded(event.getData().getPath(), event.getData().getData());

                    logger.info("TreeNode added: " + ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
                            + new String(event.getData().getData()));
                    break;
                }
                case NODE_UPDATED: {
                    this.zkListener.onNodeUpdated(event.getData().getPath(), event.getData().getData());

                    logger.info("TreeNode changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
                            + new String(event.getData().getData()));
                    break;
                }
                case NODE_REMOVED: {
                    this.zkListener.onNodeRemoved(event.getData().getPath(), event.getData().getData());
                    logger.info("TreeNode removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                    break;
                }
                case INITIALIZED: {
                    zkListener.initeCacheFinish();
                    break;
                }
                default:
                    logger.info("Other event: " + event.getType().name());
            }
        }
    }
}


