package edu.flash3388.flashlib.robot.scheduling.triggers;

import edu.flash3388.flashlib.util.beans.BooleanSource;

public class TriggerScheduler implements Runnable {

    private final BooleanSource mCondition;
    private final Trigger mTrigger;

    private boolean mLastRunConditionMet;

    public TriggerScheduler(BooleanSource condition, Trigger trigger) {
        mCondition = condition;
        mTrigger = trigger;

        mLastRunConditionMet = false;
    }

    @Override
    public void run() {
        boolean isConditionMet = mCondition.get();

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
