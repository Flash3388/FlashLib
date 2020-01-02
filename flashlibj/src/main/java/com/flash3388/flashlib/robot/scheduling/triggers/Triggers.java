package com.flash3388.flashlib.robot.scheduling.triggers;

import java.util.function.BooleanSupplier;

public class Triggers {

    private Triggers() {}

    public static Trigger onCondition(BooleanSupplier condition) {
        Trigger trigger = new Trigger();
        trigger.addToScheduler(condition);

        return trigger;
    }
}
