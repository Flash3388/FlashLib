package com.flash3388.flashlib.statemachine.nfa.attachments;

import com.flash3388.flashlib.statemachine.Transition;

import java.util.function.BooleanSupplier;

public class AttachmentTransition {

    private final BooleanSupplier mCondition;
    private final Transition mTransition;

    public AttachmentTransition(BooleanSupplier condition, Transition transition) {
        mCondition = condition;
        mTransition = transition;
    }

    public AttachmentTransition(Transition transition) {
        this(()-> true, transition);
    }

    public void perform() {
        if (mCondition.getAsBoolean()) {
            mTransition.initiate();
        }
    }
}
