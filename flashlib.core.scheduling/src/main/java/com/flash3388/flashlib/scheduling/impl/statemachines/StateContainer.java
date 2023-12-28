package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.statemachines.State;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class StateContainer {

    private static final Logger LOGGER = Logging.getLogger("StateMachine");

    private final WeakReference<Scheduler> mScheduler;
    private final State mState;
    private final Class<?> mStateCls;

    private final Collection<State> mAllowedTransitions;
    private final Collection<Function<Scheduler, Action>> mActionSuppliers;
    private final Collection<Action> mRunningActions;
    private Runnable mCallbackOnEnter;
    private Runnable mCallbackOnExit;


    public StateContainer(WeakReference<Scheduler> scheduler,
                          State state,
                          Class<?> stateCls,
                          Collection<State> allowedTransitions,
                          Collection<Function<Scheduler, Action>> actionSuppliers,
                          Collection<Action> runningActions) {
        mScheduler = scheduler;
        mState = state;
        mStateCls = stateCls;
        mAllowedTransitions = allowedTransitions;
        mActionSuppliers = actionSuppliers;
        mRunningActions = runningActions;
        mCallbackOnEnter = null;
        mCallbackOnExit = null;
    }

    public StateContainer(WeakReference<Scheduler> scheduler,
                          State state,
                          Class<?> stateCls) {
        this(scheduler, state, stateCls,
                new ArrayList<>(4),
                new ArrayList<>(2),
                new ArrayList<>(2));
    }

    public State getState() {
        return mState;
    }

    public Class<?> getStateCls() {
        return mStateCls;
    }

    public boolean canTransitionTo(State newState) {
        return mAllowedTransitions.contains(newState);
    }

    public void allowTransitionTo(Collection<? extends State> states) {
        mAllowedTransitions.addAll(states);
    }

    public void setOnEnter(Runnable runnable) {
        mCallbackOnEnter = runnable;
    }

    public void setOnExit(Runnable runnable) {
        mCallbackOnExit = runnable;
    }

    public void addAction(Function<Scheduler, Action> action) {
        mActionSuppliers.add(action);
    }

    public void onEnter() {
        LOGGER.trace("State {} onEnter", mState.name());

        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler garbage collected");
        }

        if (mCallbackOnEnter != null) {
            mCallbackOnEnter.run();
        }

        for (Function<Scheduler, Action> actionSupplier : mActionSuppliers) {
            Action action = actionSupplier.apply(scheduler);
            if (!action.isRunning()) {
                action.start();
                mRunningActions.add(action);
            }
        }
    }

    public void onExit() {
        LOGGER.trace("State {} onExit", mState.name());

        if (mCallbackOnExit != null) {
            mCallbackOnExit.run();
        }

        for (Action action : mRunningActions) {
            action.cancel();
        }
        mRunningActions.clear();
    }
}
