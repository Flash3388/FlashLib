package com.flash3388.flashlib.statemachine;

import java.util.Arrays;
import java.util.Collection;

public interface StateMachine {

    boolean isActive(State state);

    boolean areActive(Collection<? extends State> states);

    default boolean areActive(State... states) {
        return areActive(Arrays.asList(states));
    }

    StateEditor configureState(State state);

    Transition newTransition(Collection<? extends State> states);

    default Transition newTransition(State... states) {
        return newTransition(Arrays.asList(states));
    }
}
