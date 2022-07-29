package com.flash3388.flashlib.statemachine.nfa;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Actions;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.statemachine.State;
import com.flash3388.flashlib.statemachine.Transition;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.function.BooleanSupplier;

public class TransitionImpl implements Transition {

    private final WeakReference<NfaStateMachine> mStateMachine;
    private final WeakReference<Scheduler> mScheduler;
    private final Collection<? extends State> mToStates;

    public TransitionImpl(NfaStateMachine stateMachine, WeakReference<Scheduler> scheduler, Collection<? extends State> toStates) {
        mStateMachine = new WeakReference<>(stateMachine);
        mScheduler = scheduler;
        mToStates = toStates;
    }

    @Override
    public Transition on(BooleanSupplier condition) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler garbage collected");
        }

        Trigger trigger = scheduler.newTrigger(condition);
        trigger.whenActive(Actions.instant(this::initiate).configure().setRunWhenDisabled(true).save());

        return this;
    }

    @Override
    public void initiate() {
        NfaStateMachine stateMachine = mStateMachine.get();
        if (stateMachine == null) {
            throw new IllegalStateException("statemachine garbage collected");
        }

        stateMachine.transitionTo(mToStates);
    }
}
