package com.flash3388.flashlib.scheduling.triggers;

import java.util.function.BooleanSupplier;

public final class Triggers {

    private Triggers() {}

    public static Trigger onCondition(BooleanSupplier condition) {
        SchedulerTrigger trigger = new SchedulerTrigger();
        trigger.scheduleAutoUpdate(condition);

        return trigger;
    }
}
