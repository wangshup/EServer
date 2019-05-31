package com.dd.server.utils;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @program: EServer
 * @description: quartz cron job
 * @author: wangshupeng
 * @create: 2019-02-21 10:24
 **/
public class CronScheduler {
    private static final Logger logger = LoggerFactory.getLogger(CronScheduler.class);
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

    public static void shutdownJobs() {
        try {
            scheduler.shutdown();
        } catch (Exception ex) {
            logger.error("shutdown cron jobs error!", ex);
        }
    }
}