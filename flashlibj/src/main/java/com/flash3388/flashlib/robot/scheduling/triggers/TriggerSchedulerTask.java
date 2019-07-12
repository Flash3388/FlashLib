package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.scheduling.SchedulerTask;

import java.util.function.BooleanSupplier;

public class TriggerSchedulerTask implements SchedulerTask {

    private final BooleanSupplier mCondition;
    private final Trigger mTrigger;

    public TriggerSchedulerTask(BooleanSupplier condition, Trigger trigger) {
        mCondition = condition;
        mTrigger = trigger;
    }

    @Override
    public boolean run() {
        boolean isConditionMet = mCondition.getAsBoolean();

        if (isConditionMet) {
            mTrigger.activate();
        } else {
            mTrigger.deactivate();
        }

        return true;
    }
}
