package com.flash3388.flashlib.statemachine;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.actions.InstantAction;
import com.flash3388.flashlib.scheduling.triggers.Trigger;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class StateMachine {

    private final Map<State, StateConfiguration> mKnownStates;
    private final Set<State> mActiveStates;

    public StateMachine() {
        mKnownStates = new HashMap<>();
        mActiveStates = new HashSet<>();
    }

    public Set<State> getActiveStates() {
        return Collections.unmodifiableSet(mActiveStates);
    }

    public StateConfigurationEditor newState(State state) {
        StateConfiguration configuration = new StateConfiguration(state);
        mKnownStates.put(state, configuration);

        return new StateConfigurationEditor(state, configuration);
    }

    public TransitionConfigEditor newTransition(BooleanSupplier condition) {
        Trigger trigger = GlobalDependencies.getScheduler().newTrigger(condition);
        TransitionConfig config = new TransitionConfig(this);

        trigger.whenActive(new InstantAction() {
            @Override
            public void execute() {
                config.doTransition();
            }
        });

        return new TransitionConfigEditor(config);
    }

    public boolean canTransitionTo(State state) {
        StateConfiguration configuration = mKnownStates.get(state);
        return configuration.canTransitionToThis(mActiveStates);
    }

    public void transitionTo(State state) {
        if (!canTransitionTo(state)) {
            throw new IllegalStateException("State not eligible for transition");
        }

        StateConfiguration configuration = mKnownStates.get(state);

        for (Iterator<State> iterator = mActiveStates.iterator(); iterator.hasNext();) {
            State active = iterator.next();
            if (configuration.doesTerminate(active)) {
                iterator.remove();

                StateConfiguration activeConfiguration = mKnownStates.get(active);
                activeConfiguration.onTransitionFrom();
            }
        }
        mActiveStates.add(state);
        configuration.onTransitionTo();
    }
}
