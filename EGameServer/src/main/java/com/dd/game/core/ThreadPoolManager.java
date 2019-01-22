package com.dd.game.core;

import com.dd.game.utils.Constants;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池管理类
 *
 * @author wangsp
 */
public final class ThreadPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService commonExecutor = Executors.newFixedThreadPool(AVAILABLE_PROCESSORS + 1, new ZoneThreadFactory("RPG-GeneralPool"));
    public static final ScheduledExecutorService commonScheduleExecutor = Executors.newScheduledThreadPool(AVAILABLE_PROCESSORS, new ZoneThreadFactory("RPG-GerenalSTPool"));
    private static Scheduler scheduler;

    static {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            logger.error("create cron scheduler error!", e);
        }
    }

    private ThreadPoolManager() {
    }

    /**
     * 提交一个任务给线程池
     *
     * @param r 任务对象
     * @return future对象
     */
    public static Future<?> execute(Runnable r) {
        return commonExecutor.submit(r);
    }

    public static <T> Future<T> execute(Callable<T> r) {
        return commonExecutor.submit(r);
    }

    /**
     * 一段时间后执行一个任务
     *
     * @param r     任务对象
     * @param delay 延迟时间
     * @param tu    时间单位
     * @return future对象
     */
    public static ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit tu) {
        return commonScheduleExecutor.schedule(r, delay, tu);
    }

    /**
     * 定时执行一个任务
     *
     * @param r       任务对象
     * @param initial 初始任务开始时间
     * @param delay   任务固定定时时间
     * @param tu      时间单位
     * @return future对象
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay, TimeUnit tu) {
        return commonScheduleExecutor.scheduleAtFixedRate(r, initial, delay, tu);
    }

    public static void shutdown() {
        commonExecutor.shutdown();
        commonScheduleExecutor.shutdown();
    }

    public static void addCronJob(String jobName, Class<? extends Job> jobClass, String cron) {
        addCronJob(jobName, jobClass, cron, null);
    }

    public static void addCronJob(String jobName, Class<? extends Job> jobClass, String cron, Map<? extends String, ?> jobDatas) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, "CRON_JOB_GROUP").build();
            if (jobDatas != null) {
                jobDetail.getJobDataMap().putAll(jobDatas);
            }
            Trigger trigger = TriggerBuilder.newTrigger()//创建一个新的TriggerBuilder来规范一个触发器
                    .withIdentity("Trigger_" + jobName, "CRON_TRIGGER_GROUP")//给触发器起一个名字和组名
                    .startNow()//立即执行
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();//产生触发器

            //向Scheduler中添加job任务和trigger触发器
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.error("start cron job [name:{}, job class:{} error!", jobName, jobClass, e);
        }
    }

    public static boolean removeCronJob(String jobName) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey("Trigger_" + jobName, "CRON_TRIGGER_GROUP");
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            boolean bUnSchedule = scheduler.unscheduleJob(triggerKey);// 移除触发器
            boolean bDelete = scheduler.deleteJob(JobKey.jobKey(jobName, "CRON_JOB_GROUP"));// 删除任务
            return bUnSchedule || bDelete;
        } catch (Exception e) {
            logger.error("remove cron job {} error!", jobName, e);
        }
        return false;
    }

    public static class ZoneThreadFactory implements ThreadFactory {
        private ThreadFactory fac;
        private AtomicInteger threadCountor;

        public ZoneThreadFactory(final String name) {
            threadCountor = new AtomicInteger(0);
            this.fac = (r) -> new Thread(r, name + "-" + threadCountor.incrementAndGet());
        }

        public ZoneThreadFactory(final String name, int index) {
            threadCountor = new AtomicInteger(index);
            this.fac = (r) -> new Thread(r, name + "-" + threadCountor.incrementAndGet());
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = fac.newThread(r);
            if (t.getName().indexOf(Constants.SERVER_ID) == -1) {
                t.setName(String.format("%s [%s]", t.getName(), Constants.SERVER_ID));
            }
            return t;
        }
    }
}
