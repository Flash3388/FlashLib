package com.flash3388.flashlib.statemachine.nfa.attachments;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.Collection;

public abstract class ActionAttachment extends ActionBase {

    protected final Action mAction;
    private final Collection<AttachmentTransition> mTransitionsOnFinish;

    protected ActionAttachment(Scheduler scheduler, Action action, Collection<AttachmentTransition> transitionsOnFinish) {
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

        mTransitionsOnFinish.forEach(AttachmentTransition::perform);
    }

    public abstract void onStateEnter();
    public abstract void onStateExit();
}
