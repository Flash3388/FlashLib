package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.Collection;

public class AttachedAction extends ActionBase {

    private final Action mWrapperAction;
    private final Collection<AttachmentTransition> mTransitions;

    public AttachedAction(Scheduler scheduler, Action wrapperAction, Collection<AttachmentTransition> transitions) {
        super(scheduler);
        mWrapperAction = wrapperAction;
        mTransitions = transitions;

        setConfiguration(wrapperAction.getConfiguration());
    }

    @Override
    public void initialize(ActionControl control) {
        mWrapperAction.initialize(control);
    }

    @Override
    public void execute(ActionControl control) {
        mWrapperAction.execute(control);
    }

    @Override
    public void end(FinishReason reason) {
        mWrapperAction.end(reason);

        for (AttachmentTransition transition : mTransitions) {
            if (transition.transitionIfNeeded(reason)) {
                break;
            }
        }
    }
}
