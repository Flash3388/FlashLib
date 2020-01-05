package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.actions.Action;

import java.util.function.BooleanSupplier;

public class TriggerActivationAction extends Action {

    private final BooleanSupplier mCondition;
    private final Trigger mTrigger;

    public TriggerActivationAction(Scheduler scheduler, BooleanSupplier condition, Trigger trigger) {
        super(scheduler);
        mCondition = condition;
        mTrigger = trigger;
    }

    public TriggerActivationAction(BooleanSupplier condition, Trigger trigger) {
        this(RunningRobot.getInstance().getScheduler(), condition, trigger);
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
