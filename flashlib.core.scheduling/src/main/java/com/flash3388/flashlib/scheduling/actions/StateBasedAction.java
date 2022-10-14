package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class StateBasedAction<S> extends ActionBase {

    private final Clock mClock;
    private final Logger mLogger;

    private final Supplier<S> mStateSupplier;
    private final Map<S, Action> mActionForState;
    private S mFinishState;
    private S mCurrentState;
    private ActionContext mCurrentAction;

    StateBasedAction(Clock clock, Logger logger, Supplier<S> stateSupplier) {
        mClock = clock;
        mLogger = logger;
        mStateSupplier = stateSupplier;
        mActionForState = new HashMap<>();
        mFinishState = null;
        mCurrentState = null;
        mCurrentAction = null;
    }

    public StateBasedAction(Supplier<S> stateSupplier) {
        this(GlobalDependencies.getClock(), GlobalDependencies.getLogger(), stateSupplier);
    }

    public StateBasedAction<S> executeOnState(S state, Action action) {
        Objects.requireNonNull(action, "action is null");
        if (isRunning()) {
            throw new IllegalStateException("cannot modify when running");
        }

        ActionConfiguration configuration = action.getConfiguration();
        configure()
                .requires(configuration.getRequirements())
                .save();

        mActionForState.put(state, action);
        return this;
    }

    public StateBasedAction<S> finishOnState(S state) {
        mFinishState = state;
        return this;
    }

    @Override
    public void initialize() {
        mCurrentState = null;
        mCurrentAction = null;

        updateState();
    }

    @Override
    public void execute() {
        updateState();
        handleCurrentAction();
    }

    @Override
    public boolean isFinished() {
        return mFinishState != null && Objects.equals(mFinishState, mCurrentState);
    }

    @Override
    public void end(boolean wasInterrupted) {
        if (wasInterrupted && mCurrentAction != null) {
            mLogger.debug("StatedBasedAction {} interrupted, canceling current action {}", this, mCurrentAction);
            cancelCurrentAction();
        }
    }

    private void updateState() {
        S state = mStateSupplier.get();
        if (state != mCurrentState) {
            mLogger.debug("StatedBasedAction {} switching from state {} to {}", this, mCurrentState, state);
            mCurrentState = state;
            cancelCurrentAction();
            startStateAction();
        }
    }

    private void startStateAction() {
        Action newAction = mActionForState.get(mCurrentState);
        if (newAction == null) {
            mLogger.debug("State {} has no assigned action", mCurrentAction);
            return;
        }

        mCurrentAction = new ActionContext(newAction, mClock);
        mCurrentAction.prepareForRun();

        mLogger.debug("StatedBasedAction {} started action {}", this, mCurrentAction);
    }

    private void handleCurrentAction() {
        if (mCurrentAction == null) {
            return;
        }

        if (!mCurrentAction.run()) {
            mCurrentAction.runFinished();
            mLogger.debug("StatedBasedAction {} finished action {}", this, mCurrentAction);
            mCurrentAction = null;
        }
    }

    private void cancelCurrentAction() {
        if (mCurrentAction != null) {
            mCurrentAction.runCanceled();
            mLogger.debug("StatedBasedAction {} canceled action {}", this, mCurrentAction);
            mCurrentAction = null;
        }
    }
}
