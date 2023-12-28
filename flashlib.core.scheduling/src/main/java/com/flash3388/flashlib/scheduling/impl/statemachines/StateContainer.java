package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.statemachines.State;
import com.flash3388.flashlib.scheduling.statemachines.Transition;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class StateContainer {

    private static final Logger LOGGER = Logging.getLogger("StateMachine");

    private final WeakReference<Scheduler> mScheduler;
    private final State mState;
    private final Class<?> mStateCls;

    private final Collection<State> mAllowedTransitions;
    private final Collection<AttachedAction> mActions;
    private final Collection<Runnable> mCallbacksOnEnter;
    private final Collection<Runnable> mCallbacksOnExit;


    public StateContainer(WeakReference<Scheduler> scheduler,
                          State state,
                          Class<?> stateCls,
                          Collection<State> allowedTransitions,
                          Collection<AttachedAction> actions,
                          Collection<Runnable> callbacksOnEnter,
                          Collection<Runnable> callbacksOnExit) {
        mScheduler = scheduler;
        mState = state;
        mStateCls = stateCls;
        mAllowedTransitions = allowedTransitions;
        mActions = actions;
        mCallbacksOnEnter = callbacksOnEnter;
        mCallbacksOnExit = callbacksOnExit;
    }

    public StateContainer(WeakReference<Scheduler> scheduler,
                          State state,
                          Class<?> stateCls) {
        this(scheduler, state, stateCls,
                new ArrayList<>(4),
                new ArrayList<>(2),
                new ArrayList<>(1),
                new ArrayList<>(1));
    }

    public State getState() {
        return mState;
    }

    public Class<?> getStateCls() {
        return mStateCls;
    }

    public Collection<State> getAllowedTransitions() {
        return Collections.unmodifiableCollection(mAllowedTransitions);
    }

    public Collection<Action> getActions() {
        return Collections.unmodifiableCollection(mActions);
    }

    public Collection<Runnable> getCallbacksOnEnter() {
        return Collections.unmodifiableCollection(mCallbacksOnEnter);
    }

    public Collection<Runnable> getCallbacksOnExit() {
        return Collections.unmodifiableCollection(mCallbacksOnExit);
    }

    public boolean canTransitionTo(State newState) {
        return mAllowedTransitions.contains(newState);
    }

    public void allowTransitionTo(State state) {
        mAllowedTransitions.add(state);
    }

    public void allowTransitionTo(Collection<? extends State> states) {
        mAllowedTransitions.addAll(states);
    }

    public void addAction(Action action) {
        addAction(action, null);
    }

    public void addAction(Action action, Transition onFinish) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler garbage collected");
        }

        mActions.add(new AttachedAction(scheduler, action, onFinish));
    }

    public void addOnEnter(Runnable runnable) {
        mCallbacksOnEnter.add(runnable);
    }

    public void addOnExit(Runnable runnable) {
        mCallbacksOnExit.add(runnable);
    }

    public void onEnter() {
        LOGGER.trace("State {} onEnter", mState.name());
    }

    public void onExit() {
        LOGGER.trace("State {} onExit", mState.name());
    }
}
