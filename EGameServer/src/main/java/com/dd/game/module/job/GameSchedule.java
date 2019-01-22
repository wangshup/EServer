package com.dd.game.module.job;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class GameSchedule {

    private static final GameSchedule INSTANCE = new GameSchedule();

    public Scheduler sched;
    private AtomicInteger id = new AtomicInteger();

    public static GameSchedule getInstance() {
        return INSTANCE;
    }

    private GameSchedule() {
    }

    public void start() {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            sched = sf.getScheduler();

            addJob(HourJob.class, "0 0 * * * ?");

            sched.start();
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    public void stop() {
        if (sched != null) {
            try {
                sched.shutdown(true);
            } catch (Exception e) {
                log.error("ERROR", e);
            }
        }
    }

    private void addJob(Class<? extends Job> jobClazz, String cron) {
        try {
            int newId = id.incrementAndGet();

            JobDetail job = JobBuilder.newJob(jobClazz).withIdentity("job_" + newId, "group1").build();

            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger_" + newId, "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
            sched.scheduleJob(job, trigger);

        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    private static final Log log = LogFactory.getLog(GameSchedule.class);
}
