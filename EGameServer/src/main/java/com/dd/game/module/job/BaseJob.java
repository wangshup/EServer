package com.dd.game.module.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;

public abstract class BaseJob implements Job {

    /**
     * true:当前正在执行job
     */
    private boolean processing = false;

    public void execute(org.quartz.JobExecutionContext arg0) throws org.quartz.JobExecutionException {
        run();
    }

    public void run() {
        if (processing) {
            // 如果在一个周期内未执行完，需要特别关注，并查找原因。
            log.error("job is processing ... skip.");
            return;
        }
        log.info("processing job.");
        processing = true;
        long startTime = System.currentTimeMillis();
        try {
            doWork();
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        long endTime = System.currentTimeMillis();
        log.info("processing used time:" + (endTime - startTime));
        processing = false;
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    /**
     * 子类实现的具体业务逻辑接口
     */
    protected abstract void doWork();

    protected Log log = LogFactory.getLog(getClass());
}
