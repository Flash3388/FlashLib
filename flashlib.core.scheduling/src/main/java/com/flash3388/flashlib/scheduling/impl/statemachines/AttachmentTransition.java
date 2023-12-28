package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.statemachines.Transition;

import java.util.Set;

public class AttachmentTransition {

    private final Transition mTransition;
    private final Set<FinishReason> mFinishReasons;

    public AttachmentTransition(Transition transition, Set<FinishReason> finishReasons) {
        mTransition = transition;
        mFinishReasons = finishReasons;
    }

    public AttachmentTransition(Transition transition) {
        this(transition, null);
    }

    public boolean transitionIfNeeded(FinishReason reason) {
        if (mFinishReasons != null && mFinishReasons.contains(reason)) {
            mTransition.initiate();
            return true;
        }

        return false;
    }
}
