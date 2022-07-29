package com.flash3388.flashlib.statemachine.nfa.attachments;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.flash3388.flashlib.statemachine.Transition;

import java.util.Set;

public abstract class ActionAttachment extends ActionBase {

    protected final Action mAction;
    private final Set<Transition> mTransitionsOnFinish;

    protected ActionAttachment(Scheduler scheduler, Action action, Set<Transition> transitionsOnFinish) {
        super(scheduler);
        mAction = action;
        mTransitionsOnFinish = transitionsOnFinish;

        setConfiguration(mAction.getConfiguration());
    }

    @Override
    public void initialize() {
        mAction.initialize();
    }

    @Override
    public void execute() {
        mAction.execute();
    }

    @Override
    public boolean isFinished() {
        return mAction.isFinished();
    }

    @Override
    public void end(boolean wasInterrupted) {
        mAction.end(wasInterrupted);

        mTransitionsOnFinish.forEach(Transition::initiate);
    }

    public abstract void onStateEnter();
    public abstract void onStateExit();
}
