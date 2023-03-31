package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionInterface;

import java.util.function.BooleanSupplier;

public class TriggerActivationAction implements ActionInterface {

    private final BooleanSupplier mCondition;
    private final TriggerImpl mTrigger;

    public TriggerActivationAction(BooleanSupplier condition, TriggerImpl trigger) {
        mCondition = condition;
        mTrigger = trigger;
    }

    @Override
    public void execute(ActionControl control) {
        boolean isConditionMet = mCondition.getAsBoolean();

        if (isConditionMet) {
            mTrigger.activate();
        } else {
            mTrigger.deactivate();
        }
    }
}
