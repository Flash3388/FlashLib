package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.statemachines.State;
import com.flash3388.flashlib.scheduling.statemachines.Transition;

import java.lang.ref.WeakReference;
import java.util.Collection;

public class TransitionImpl implements Transition {

    private final WeakReference<StateMachineImpl> mParentMachine;
    private final Collection<? extends State> mToStates;

    public TransitionImpl(StateMachineImpl parentMachine, Collection<? extends State> toStates) {
        mParentMachine = new WeakReference<>(parentMachine);
        mToStates = toStates;
    }

    @Override
    public void initiate() {
        StateMachineImpl stateMachine = mParentMachine.get();
        if (stateMachine == null) {
            throw new IllegalStateException("statemachine garbage collected");
        }

        stateMachine.transitionTo(mToStates);
    }
}
