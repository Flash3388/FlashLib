package com.flash3388.flashlib.scheduling.statemachines;

import java.util.Arrays;
import java.util.Collection;

public interface StateMachine {

    <T extends Enum<?> & State> StateConfigurer configure(T state);

    void configureIdleStates(Collection<? extends State> states);

    default void configureIdleStates(State... states) {
        configureIdleStates(Arrays.asList(states));
    }

    Transition newTransition(Collection<? extends State> states);

    default Transition newTransition(State... states) {
        return newTransition(Arrays.asList(states));
    }

    boolean isActive(Collection<? extends State> states);

    default boolean isActive(State... states) {
        return isActive(Arrays.asList(states));
    }

    void reset(Collection<? extends State> states);

    default void reset(State... states) {
        reset(Arrays.asList(states));
    }

    void resetToIdleStates();
}
