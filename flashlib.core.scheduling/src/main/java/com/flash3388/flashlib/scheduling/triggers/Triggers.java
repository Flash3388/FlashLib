package com.flash3388.flashlib.scheduling.triggers;

import java.util.function.BooleanSupplier;

/**
 * A utility for working with {@link Trigger}.
 *
 * @since FlashLib 1.2.0
 */
public final class Triggers {

    private Triggers() {}

    /**
     * Creates a new {@link Trigger} whose state is determined by a {@link BooleanSupplier}.
     *
     * @param condition the state for the trigger. When {@link BooleanSupplier#getAsBoolean()} is <b>true</b>
     *                  the trigger will be active, when it is <b>false</b> inactive.
     *
     * @return a trigger
     */
    public static Trigger onCondition(BooleanSupplier condition) {
        SchedulerTrigger trigger = new SchedulerTrigger();
        trigger.scheduleAutoUpdate(condition);

        return trigger;
    }
}
