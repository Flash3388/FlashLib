package com.flash3388.flashlib.statemachine.nfa.attachments;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.statemachine.*;
import com.flash3388.flashlib.statemachine.nfa.StateConfiguration;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ActionAttachmentEditorImpl implements ActionAttachmentEditor {

    private final WeakReference<StateMachine> mStateMachine;
    private final StateEditor mPreviousMenu;
    private final StateConfiguration mConfiguration;
    private final Action mAction;

    private final Set<Transition> mTransitionsOnFinish;

    public ActionAttachmentEditorImpl(WeakReference<StateMachine> stateMachine, StateEditor previousMenu, StateConfiguration configuration, Action action) {
        mStateMachine = stateMachine;
        mPreviousMenu = previousMenu;
        mConfiguration = configuration;
        mAction = action;

        mTransitionsOnFinish = new HashSet<>();
    }

    @Override
    public ActionAttachmentEditor transitionOnFinish(Collection<? extends State> states) {
        StateMachine stateMachine = mStateMachine.get();
        if (stateMachine == null) {
            throw new IllegalStateException("statemachine garbage collected");
        }

        mTransitionsOnFinish.add(stateMachine.newTransition(states));
        return this;
    }

    @Override
    public StateEditor runWhileStateActive() {
        mConfiguration.attachAction(new RunWhileActiveAttachment(mAction, mTransitionsOnFinish));
        return mPreviousMenu;
    }
}
