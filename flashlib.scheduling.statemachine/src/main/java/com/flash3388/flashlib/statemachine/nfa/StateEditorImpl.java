package com.flash3388.flashlib.statemachine.nfa;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.statemachine.ActionAttachmentEditor;
import com.flash3388.flashlib.statemachine.State;
import com.flash3388.flashlib.statemachine.StateEditor;
import com.flash3388.flashlib.statemachine.StateMachine;
import com.flash3388.flashlib.statemachine.nfa.attachments.ActionAttachmentEditorImpl;

import java.lang.ref.WeakReference;
import java.util.Collection;

public class StateEditorImpl implements StateEditor {

    private final WeakReference<StateMachine> mStateMachine;
    private final WeakReference<Scheduler> mScheduler;
    private final StateConfiguration mConfiguration;

    public StateEditorImpl(WeakReference<StateMachine> stateMachine, WeakReference<Scheduler> scheduler, StateConfiguration configuration) {
        mStateMachine = stateMachine;
        mScheduler = scheduler;
        mConfiguration = configuration;
    }

    @Override
    public StateEditor allowTransitionTo(Collection<? extends State> states) {
        mConfiguration.allowTransition(states);
        return this;
    }

    @Override
    public ActionAttachmentEditor attachAction(Action action) {
        return new ActionAttachmentEditorImpl(mStateMachine, mScheduler, this, mConfiguration, action);
    }
}
