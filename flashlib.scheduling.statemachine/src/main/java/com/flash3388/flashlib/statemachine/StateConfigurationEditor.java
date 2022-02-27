package com.flash3388.flashlib.statemachine;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StateConfigurationEditor {

    private final State mState;
    private final StateConfiguration mConfiguration;

    public StateConfigurationEditor(State state, StateConfiguration configuration) {
        mState = state;
        mConfiguration = configuration;
    }

    public StateConfigurationEditor requires(State... states) {
        return requires(new HashSet<>(Arrays.asList(states)));
    }

    public StateConfigurationEditor requires(Set<? extends State> states) {
        mConfiguration.requires(states);
        return this;
    }

    public StateConfigurationEditor terminates(State... states) {
        return terminates(new HashSet<>(Arrays.asList(states)));
    }

    public StateConfigurationEditor terminates(Set<? extends State> states) {
        mConfiguration.terminates(states);
        return this;
    }

    public StateConfigurationEditor blocking(State... states) {
        return blocking(new HashSet<>(Arrays.asList(states)));
    }

    public StateConfigurationEditor blocking(Set<? extends State> states) {
        mConfiguration.blocking(states);
        return this;
    }

    public StateConfigurationEditor whileActive(Action action) {
        mConfiguration.whileActive(action);
        return this;
    }
}
