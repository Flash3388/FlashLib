package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

public class ActionContext2 {

    private final ActionInterface mAction;
    private final Clock mClock;
    private final Logger mLogger;

    private final ObsrActionContext mObsrActionContext;
    private final ActionExecutionState mExecutionState;

    private ActionConfiguration mConfiguration;
    private ActionControl mControl;
    private boolean mIsInitialized;
    private boolean mIsConfigured;

    public ActionContext2(ActionInterface action, ActionConfiguration configuration,
                          StoredObject obsrObject,
                          Clock clock,
                          Logger logger) {
        mAction = action;
        mClock = clock;
        mLogger = logger;

        mObsrActionContext = new ObsrActionContext(obsrObject);
        mExecutionState = new ActionExecutionState(mObsrActionContext, clock);

        mConfiguration = configuration;
        mControl = null;
        mIsInitialized = false;
        mIsConfigured = false;
    }

    public void markStarted() {
        verifyConfigured();
        mExecutionState.markStarted(mConfiguration);
    }

    public void markForCancellation() {
        verifyConfigured();
        mExecutionState.markForCancellation();
    }

    public boolean iterate() {
        verifyConfigured();

        if (mExecutionState.isTimedOut()) {
            mExecutionState.markTimedOut();
        }

        if (mExecutionState.isMarkedForEnd()) {
            finish();
            return true;
        }

        if (!mIsInitialized) {
            initialize();
            mIsInitialized = true;
        } else {
            execute();

            if (mExecutionState.isMarkedForEnd()) {
                finish();
                return true;
            }
        }

        if (mExecutionState.isMarkedForEnd()) {
            finish();
            return true;
        }

        return false;
    }

    public ActionConfiguration configure() {
        ActionConfigurerImpl configurer = new ActionConfigurerImpl(mConfiguration);
        try {
            mLogger.trace("Calling configure for {}", this);
            mAction.configure(configurer);
        } catch (Throwable t) {
            mLogger.warn("Error occurred during configuring phase of action", t);
            throw new ActionConfigurationException(t);
        }

        ActionConfiguration configuration = configurer.save();
        finishConfiguring(configuration);

        return configuration;
    }

    private void initialize() {
        try {
            mLogger.trace("Calling initialize for {}", this);
            mAction.initialize(mControl);
        } catch (Throwable t) {
            mLogger.warn("Error occurred during initialization phase of action", t);
            mExecutionState.markErrored();
        }

        mExecutionState.updatePhase(ExecutionPhase.EXECUTION);
    }

    private void execute() {
        try {
            mLogger.trace("Calling execute for {}", this);
            mAction.execute(mControl);
        } catch (Throwable t) {
            mLogger.warn("Error occurred during execution phase of action", t);
            mExecutionState.markErrored();
        }
    }

    private void finish() {
        mExecutionState.updatePhase(ExecutionPhase.END);

        try {
            mLogger.trace("Calling end for {}", this);
            mAction.end(mExecutionState.getFinishReason());
            mExecutionState.markFinishedExecution();
        } catch (Throwable t) {
            mLogger.warn("Error occurred in action during the end phase, trying again", t);
            mExecutionState.markErrored();

            // try doing it again, so we can maybe make sure end runs.
            try {
                mLogger.debug("Re-Calling end for {}", this);
                mAction.end(mExecutionState.getFinishReason());
            } catch (Throwable t1) {
                // oh, well not much we can do
                mLogger.warn("Error occurred AGAIN in action during end phase. Terminating forcibly. " +
                        "It is likely the action has not finished it's teardown properly", t1);
            }

            mExecutionState.markFinishedExecution();
        }
    }

    private void finishConfiguring(ActionConfiguration configuration) {
        mConfiguration = configuration;
        mControl = new ActionControlImpl(mAction, mConfiguration,
                mExecutionState,
                mObsrActionContext,
                mClock,
                mLogger);
        mIsConfigured = true;
    }

    private void verifyConfigured() {
        if (!mIsConfigured) {
            throw new IllegalStateException("cannot run when not configured");
        }
    }

    @Override
    public String toString() {
        String clsName = mAction.getClass().getSimpleName();
        if (clsName.isEmpty()) {
            clsName = mAction.getClass().getName();
        }

        if (mIsConfigured) {
            return String.format("%s{name=%s}", clsName, mConfiguration.getName());
        } else {
            return String.format("%s{NOT CONFIGURED}", clsName);
        }
    }
}
