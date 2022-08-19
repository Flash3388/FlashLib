package com.flash3388.flashlib.statemachine.nfa.attachments;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.statemachine.Transition;

import java.util.Collection;
import java.util.Set;

public class RunWhileActiveAttachment extends ActionAttachment {

    public RunWhileActiveAttachment(Scheduler scheduler, Action action, Collection<AttachmentTransition> transitionsOnFinish) {
        super(scheduler, action, transitionsOnFinish);
    }

    @Override
    public void onStateEnter() {
        if (!isRunning()) {
            start();
        }
    }

    @Override
    public void onStateExit() {
        if (isRunning()) {
            cancel();
        }
    }
}
