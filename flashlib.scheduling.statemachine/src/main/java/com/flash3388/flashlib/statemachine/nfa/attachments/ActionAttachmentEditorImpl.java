package com.flash3388.flashlib.statemachine.nfa.attachments;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.statemachine.*;
import com.flash3388.flashlib.statemachine.nfa.StateConfiguration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class ActionAttachmentEditorImpl implements ActionAttachmentEditor {

    private final WeakReference<StateMachine> mStateMachine;
    private final WeakReference<Scheduler> mScheduler;
    private final StateEditor mPreviousMenu;
    private final StateConfiguration mConfiguration;
    private final Action mAction;

    private final Collection<AttachmentTransition> mTransitionsOnFinish;

    public ActionAttachmentEditorImpl(WeakReference<StateMachine> stateMachine, WeakReference<Scheduler> scheduler,
                                      StateEditor previousMenu, StateConfiguration configuration, Action action) {
        mStateMachine = stateMachine;
        mScheduler = scheduler;
        mPreviousMenu = previousMenu;
        mConfiguration = configuration;
        mAction = action;

        mTransitionsOnFinish = new ArrayList<>();
    }

    @Override
    public ActionAttachmentEditor transitionOnFinish(BooleanSupplier condition, Collection<? extends State> states) {
        StateMachine stateMachine = mStateMachine.get();
        if (stateMachine == null) {
            throw new IllegalStateException("statemachine garbage collected");
        }

        Transition transition = stateMachine.newTransition(states);
        mTransitionsOnFinish.add(new AttachmentTransition(condition, transition));
        return this;
    }

    @Override
    public ActionAttachmentEditor transitionOnFinish(Collection<? extends State> states) {
        StateMachine stateMachine = mStateMachine.get();
        if (stateMachine == null) {
            throw new IllegalStateException("statemachine garbage collected");
        }

        Transition transition = stateMachine.newTransition(states);
        mTransitionsOnFinish.add(new AttachmentTransition(transition));
        return this;
    }

    @Override
    public StateEditor runWhileStateActive() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler garbage collected");
        }

        mConfiguration.attachAction(new RunWhileActiveAttachment(scheduler, mAction, mTransitionsOnFinish));
        return mPreviousMenu;
    }
}
