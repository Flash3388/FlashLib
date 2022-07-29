package com.flash3388.flashlib.statemachine.nfa.attachments;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.statemachine.Transition;

import java.util.Set;

public class RunWhileActiveAttachment extends ActionAttachment {

    public RunWhileActiveAttachment(Action action, Set<Transition> transitionsOnFinish) {
        super(action, transitionsOnFinish);
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
