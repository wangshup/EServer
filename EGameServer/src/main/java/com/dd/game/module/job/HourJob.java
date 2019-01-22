package com.dd.game.module.job;

import com.dd.game.module.event.EventDispatcher;
import com.dd.game.module.event.EventType;

public class HourJob extends BaseJob {

    @Override
    protected void doWork() {
        try {
            freshHour();
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    private void freshHour() {
        EventDispatcher.fire(EventType.HOUR_TRIGGER);
    }

}
