package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.actions.ActionBase;

import java.util.function.BooleanSupplier;

public class TriggerActivationAction extends ActionBase {

    private final BooleanSupplier mCondition;
    private final SchedulerTrigger mTrigger;

    public TriggerActivationAction(Scheduler scheduler, BooleanSupplier condition, SchedulerTrigger trigger) {
        super(scheduler);
        mCondition = condition;
        mTrigger = trigger;

        configure()
                .requires(mTrigger)
                .setRunWhenDisabled(true)
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
}
