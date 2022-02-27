package com.flash3388.flashlib.statemachine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransitionConfig {

    private final StateMachine mStateMachine;
    private final Map<Set<State>, Set<State>> mTransitionMap;

    public TransitionConfig(StateMachine stateMachine) {
        mStateMachine = stateMachine;
        mTransitionMap = new HashMap<>();
    }

    public void addTransition(Set<State> active, Set<State> to) {
        mTransitionMap.put(active, to);
    }

    public void doTransition() {
        Set<State> active = mStateMachine.getActiveStates();
        for (Map.Entry<Set<State>, Set<State>> entry : mTransitionMap.entrySet()) {
            if (active.containsAll(entry.getKey())) {
                for (State dest : entry.getValue()) {
                    mStateMachine.transitionTo(dest);
                }
            }
        }
    }
}
