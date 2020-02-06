package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.actions.ActionBase;

import java.util.function.BooleanSupplier;

public class TriggerActivationAction extends ActionBase {

    private final BooleanSupplier mCondition;
    private final Trigger mTrigger;

    public TriggerActivationAction(Scheduler scheduler, BooleanSupplier condition, Trigger trigger) {
        super(scheduler);
        mCondition = condition;
        mTrigger = trigger;

        configure().requires(mTrigger)
                .save();
    }

    @Override
    public void execute() {
        boolean isConditionMet = mCondition.getAsBoolean();

        if (isConditionMet) {
            mTrigger.activate();
        } else {
            mTrigger.deactivate();
        }
    }

    @Override
    public boolean runWhenDisabled() {
        return true;
    }
}
