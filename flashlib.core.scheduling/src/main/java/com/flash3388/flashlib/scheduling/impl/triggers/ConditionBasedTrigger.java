package com.flash3388.flashlib.scheduling.impl.triggers;

import java.util.function.BooleanSupplier;

public class ConditionBasedTrigger extends TriggerBaseImpl implements GenericTrigger {

    private final BooleanSupplier mCondition;

    public ConditionBasedTrigger(BooleanSupplier condition) {
        mCondition = condition;
    }

    @Override
    public void update(TriggerActionController controller) {
        boolean isConditionMet = mCondition.getAsBoolean();
        setState(isConditionMet ? TriggerState.ACTIVE : TriggerState.INACTIVE, controller);
    }
}
