package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.scheduling.Trigger;

import java.util.function.BooleanSupplier;

public final class Triggers {

    private Triggers() {}

    public static Trigger onCondition(BooleanSupplier condition) {
        SchedulerTrigger trigger = new SchedulerTrigger();
        trigger.schedule(condition);

        return trigger;
    }
}
