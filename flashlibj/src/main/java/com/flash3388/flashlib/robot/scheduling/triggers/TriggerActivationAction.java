package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.scheduling.actions.Action;

import java.util.function.BooleanSupplier;

public class TriggerActivationAction extends Action {

    private final BooleanSupplier mCondition;
    private final Trigger mTrigger;

    public TriggerActivationAction(BooleanSupplier condition, Trigger trigger) {
        mCondition = condition;
        mTrigger = trigger;
    }

    @Override
    protected void execute() {
        boolean isConditionMet = mCondition.getAsBoolean();

        if (isConditionMet) {
            mTrigger.activate();
        } else {
            mTrigger.deactivate();
        }
    }

    @Override
    protected void end() {
    }

    @Override
    public boolean runWhenDisabled() {
        return true;
    }
}
