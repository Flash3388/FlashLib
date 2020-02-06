package com.flash3388.flashlib.robot.scheduling.triggers;

import java.util.function.BooleanSupplier;

public final class Triggers {

    private Triggers() {}

    public static Trigger onCondition(BooleanSupplier condition) {
        Trigger trigger = new Trigger();
        trigger.schedule(condition);

        return trigger;
    }
}
