package com.flash3388.flashlib.statemachine;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.HashSet;
import java.util.Set;

public class StateConfiguration {

    private final State mState;
    private final Set<State> mRequires;
    private final Set<State> mTerminates;
    private final Set<State> mBlocking;
    private final Set<Action> mActionsToActivate;

    public StateConfiguration(State state) {
        mState = state;
        mRequires = new HashSet<>();
        mTerminates = new HashSet<>();
        mBlocking = new HashSet<>();
        mActionsToActivate = new HashSet<>();
    }

    public boolean doesTerminate(State state) {
        return mTerminates.contains(state);
    }

    public void requires(Set<? extends State> states) {
        mRequires.addAll(states);
    }

    public void terminates(Set<? extends State> states) {
        mTerminates.addAll(states);
    }

    public void blocking(Set<? extends State> states) {
        mBlocking.addAll(states);
    }

    public void whileActive(Action action) {
        mActionsToActivate.add(action);
    }

    public boolean canTransitionToThis(Set<State> active) {
        if (!active.containsAll(mRequires)) {
            return false;
        }

        for (State state : active) {
            if (mBlocking.contains(state)) {
                return false;
            }
        }

        return true;
    }

    public void onTransitionTo() {
        mActionsToActivate.forEach(Action::start);
    }

    public void onTransitionFrom() {
        mActionsToActivate.forEach(Action::cancel);
    }
}
