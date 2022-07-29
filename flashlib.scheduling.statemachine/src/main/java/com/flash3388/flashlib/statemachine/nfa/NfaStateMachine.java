package com.flash3388.flashlib.statemachine.nfa;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.statemachine.*;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class NfaStateMachine implements StateMachine {

    private final WeakReference<Scheduler> mScheduler;
    private final Map<State, StateConfiguration> mKnownStates;
    private final Set<State> mActiveStates;

    public NfaStateMachine(Scheduler scheduler) {
        mScheduler = new WeakReference<>(scheduler);
        mKnownStates = new HashMap<>();
        mActiveStates = new HashSet<>();
    }

    public NfaStateMachine() {
        this(GlobalDependencies.getScheduler());
    }

    @Override
    public boolean isActive(State state) {
        return mActiveStates.contains(state);
    }

    @Override
    public boolean areActive(Collection<? extends State> states) {
        return mActiveStates.containsAll(states);
    }

    @Override
    public StateEditor configureState(State state) {
        StateConfiguration configuration = new StateConfiguration(state);
        mKnownStates.put(state, configuration);
        return new StateEditorImpl(new WeakReference<>(this), mScheduler, configuration);
    }

    @Override
    public Transition newTransition(Collection<? extends State> states) {
        return new TransitionImpl(this, mScheduler, states);
    }

    void transitionTo(Collection<? extends State> states) {
        Set<State> transitioning = collectTransitioningStates(states);
        for (State active : transitioning) {
            StateConfiguration configuration = mKnownStates.get(active);
            configuration.onExit();
        }

        for (State newState : states) {
            if (mActiveStates.contains(newState)) {
                continue; // already active
            }

            StateConfiguration configuration = mKnownStates.get(newState);
            configuration.onEnter();
        }

        mActiveStates.removeAll(transitioning);
        mActiveStates.addAll(states);
    }

    private Set<State> collectTransitioningStates(Collection<? extends State> newStates) {
        Set<State> transitioning = new HashSet<>();
        for (State active : mActiveStates) {
            StateConfiguration configuration = mKnownStates.get(active);
            if (configuration.canTransitionToAnyOf(newStates)) {
                transitioning.add(active);
            }
        }

        return transitioning;
    }
}
