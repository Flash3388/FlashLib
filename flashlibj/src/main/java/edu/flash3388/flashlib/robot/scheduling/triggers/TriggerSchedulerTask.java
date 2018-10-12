package edu.flash3388.flashlib.robot.scheduling.triggers;

import java.util.function.BooleanSupplier;

public class TriggerSchedulerTask implements Runnable {

    private final BooleanSupplier mCondition;
    private final Trigger mTrigger;

    private boolean mLastRunConditionMet;

    public TriggerSchedulerTask(BooleanSupplier condition, Trigger trigger) {
        mCondition = condition;
        mTrigger = trigger;

        mLastRunConditionMet = false;
    }

    @Override
    public void run() {
        boolean isConditionMet = mCondition.getAsBoolean();

        if (isConditionMet != mLastRunConditionMet) {
            if (isConditionMet) {
                mTrigger.activate();
            } else {
                mTrigger.deactivate();
            }
        }

        mLastRunConditionMet = isConditionMet;
    }
}
