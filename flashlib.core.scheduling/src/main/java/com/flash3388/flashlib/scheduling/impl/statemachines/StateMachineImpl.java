package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.net.obsr.StoredEntry;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.statemachines.State;
import com.flash3388.flashlib.scheduling.statemachines.StateConfigurer;
import com.flash3388.flashlib.scheduling.statemachines.Transition;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StateMachineImpl implements InternalStateMachine {

    private static final String[] EMPTY_STRING_ARR = new String[0];

    private static final Logger LOGGER = Logging.getLogger("StateMachine");

    private final WeakReference<Scheduler> mScheduler;
    private final String mName;

    private final Map<State, StateContainer> mStates;
    private final Set<State> mActiveStates;
    private final Set<State> mIdleStates;

    private final StoredEntry mActiveStatesEntry;

    private boolean mWasIdleModeInitialized;

    StateMachineImpl(Scheduler scheduler,
                     String name,
                     StoredObject rootObject,
                     Map<State, StateContainer> states,
                     Set<State> activeStates,
                     Set<State> idleStates) {
        mScheduler = new WeakReference<>(scheduler);
        mName = name;
        mStates = states;
        mActiveStates = activeStates;
        mIdleStates = idleStates;

        mActiveStatesEntry = rootObject.getEntry("activeStates");
        mActiveStatesEntry.setStringArray(EMPTY_STRING_ARR);

        mWasIdleModeInitialized = false;
    }

    public StateMachineImpl(Scheduler scheduler, String name, StoredObject rootObject) {
        this(scheduler, name, rootObject,
                new HashMap<>(10),
                new HashSet<>(5),
                new HashSet<>(2));
    }

    @Override
    public <T extends Enum<?> & State> StateConfigurer configure(T state) {
        StateContainer container = new StateContainer(mScheduler, state, state.getClass());
        mStates.put(state, container);
        return new StateConfigurerImpl(container);
    }

    @Override
    public void configureIdleStates(Collection<? extends State> states) {
        mIdleStates.clear();
        mIdleStates.addAll(states);
        mWasIdleModeInitialized = false;
    }

    @Override
    public Transition newTransition(Collection<? extends State> states) {
        verifyKnownStates(states);
        verifyNoDuplicateTypes(states);

        return new TransitionImpl(this, states);
    }

    @Override
    public boolean isActive(Collection<? extends State> states) {
        return mActiveStates.containsAll(states);
    }

    @Override
    public void reset(Collection<? extends State> states) {
        LOGGER.info("StateMachine {} resetting states to {}", mName, states);

        verifyKnownStates(states);
        verifyNoDuplicateTypes(states);

        onExit(new ArrayList<>(mActiveStates));
        onEnter(states);
    }

    @Override
    public void resetToIdleStates() {
        reset(mIdleStates);
    }

    @Override
    public void update(SchedulerMode mode) {
        if (mode.isDisabled()) {
            if (!mWasIdleModeInitialized) {
                LOGGER.info("StateMachine {} entering idle mode", mName);

                mWasIdleModeInitialized = true;
                reset(mIdleStates);
            }
        } else {
            mWasIdleModeInitialized = false;
        }
    }

    void transitionTo(Collection<? extends State> states) {
        LOGGER.info("StateMachine {} preparing transition", mName);

        verifyKnownStates(states);
        verifyNoDuplicateTypes(states);

        Collection<State> transitioning = collectTransitioningStates(states);
        LOGGER.info("StateMachine {} transition states {} to {}", mName, transitioning, states);

        onExit(transitioning);
        onEnter(states);
    }

    private void onEnter(Collection<? extends State> states) {
        for (State state : states) {
            StateContainer container = mStates.get(state);
            container.onEnter();

            mActiveStates.add(state);
        }

        Set<String> activeStateNames = mActiveStates.stream()
                .map(State::name)
                .collect(Collectors.toSet());
        mActiveStatesEntry.setStringArray(activeStateNames.toArray(EMPTY_STRING_ARR));
    }

    private void onExit(Collection<? extends State> states) {
        for (State state : states) {
            StateContainer container = mStates.get(state);
            container.onExit();

            mActiveStates.remove(state);
        }
    }

    private Collection<State> collectTransitioningStates(Collection<? extends State> newStates) {
        Set<State> transitioning = new HashSet<>();
        for (State newState : newStates) {
            StateContainer newStateContainer = mStates.get(newState);
            StateContainer oldStateContainer = findActiveStateOfType(newStateContainer.getStateCls());
            if (!oldStateContainer.canTransitionTo(newState)) {
                throw new IllegalStateException(
                        String.format("Cannot transition state %s to %s",
                                oldStateContainer.getState().name(),
                                newState.name()));
            }

            transitioning.add(oldStateContainer.getState());
        }

        return transitioning;
    }

    private StateContainer findActiveStateOfType(Class<?> stateCls) {
        for (State state : mActiveStates) {
            StateContainer container = mStates.get(state);
            if (container.getStateCls().isAssignableFrom(stateCls)) {
                return container;
            }
        }

        throw new IllegalStateException("no state of type " + stateCls.getName());
    }

    private void verifyKnownStates(Collection<? extends State> states) {
        for (State state : states) {
            if (!mStates.containsKey(state)) {
                throw new IllegalArgumentException("state is not configured: " + state);
            }
        }
    }

    private void verifyNoDuplicateTypes(Collection<? extends State> states) {
        for (State state1 : states) {
            for (State state2 : states) {
                if (state1.equals(state2)) {
                    continue;
                }

                StateContainer container1 = mStates.get(state1);
                StateContainer container2 = mStates.get(state2);

                if (container1.getStateCls().isAssignableFrom(container2.getStateCls())) {
                    throw new IllegalArgumentException("Cannot assign 2 active states of the same type");
                }
            }
        }
    }
}
