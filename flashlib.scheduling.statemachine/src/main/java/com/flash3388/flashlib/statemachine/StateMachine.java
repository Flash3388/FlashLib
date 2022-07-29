package com.flash3388.flashlib.statemachine;

import java.util.Arrays;
import java.util.Collection;

public interface StateMachine {

    StateEditor configureState(State state);

    Transition newTransition(Collection<? extends State> states);

    default Transition newTransition(State... states) {
        return newTransition(Arrays.asList(states));
    }
}
