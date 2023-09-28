package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.Action;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Set;

public class RunningActionContext {

    private final ActionInterface mAction;
    private final boolean mParent;
    private final ObsrActionContext mObsrContext;
    private final Logger mLogger;

    private final ActionConfiguration mConfiguration;
    private final ActionExecutionState mExecutionState;
    private final ActionControl mControl;

    private boolean mIsInitialized;

    public RunningActionContext(ActionInterface action,
                                ActionConfiguration configuration,
                                boolean hasParent,
                                ObsrActionContext obsrContext,
                                Clock clock,
                                Logger logger) {
        mAction = action;
        mParent = hasParent;
        mObsrContext = obsrContext;
        mLogger = logger;

        mConfiguration = new ActionConfiguration(configuration);
        mExecutionState = new ActionExecutionState(mConfiguration, obsrContext, clock, logger);
        mControl = new ActionControlImpl(action, mConfiguration, mExecutionState, obsrContext, clock, logger);

        mIsInitialized = false;

        mObsrContext.updateFromAction(action);
        mObsrContext.updateFromConfiguration(mConfiguration);
    }

    public RunningActionContext(ActionInterface action,
                                ActionConfiguration configuration,
                                ObsrActionContext obsrContext,
                                Clock clock,
                                Logger logger) {
        this(action, configuration, null, obsrContext, clock, logger);
    }

    public ActionInterface getAction() {
        return mAction;
    }

    public boolean shouldRunInDisabled() {
        return mConfiguration.hasFlags(ActionFlag.RUN_ON_DISABLED);
    }

    public boolean isPreferred() {
        return mConfiguration.hasFlags(ActionFlag.PREFERRED_FOR_REQUIREMENTS);
    }

    public Set<Requirement> getRequirements() {
        return mConfiguration.getRequirements();
    }

    public Time getRunTime() {
        return mExecutionState.getRunTime();
    }

    public void markStarted() {
        mExecutionState.markStarted();
    }

    public void markForCancellation() {
        mExecutionState.markForCancellation();
    }

    public boolean iterate() {
        if (wasTimedOut()) {
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

    private void initialize() {
        try {
            mLogger.trace("Calling initialize for {}", this);
            mAction.initialize(mControl);
        } catch (Throwable t) {
            mLogger.warn("Error occurred during initialization phase of action");
            mExecutionState.markErrored(t);
        }

        mObsrContext.updatePhase(ExecutionPhase.EXECUTION);
    }

    private void execute() {
        try {
            mLogger.trace("Calling execute for {}", this);
            mAction.execute(mControl);
        } catch (Throwable t) {
            mLogger.warn("Error occurred during execution phase of action");
            mExecutionState.markErrored(t);
        }
    }

    private void finish() {
        mObsrContext.updatePhase(ExecutionPhase.END);

        try {
            mLogger.trace("Calling end for {}", this);
            mAction.end(mExecutionState.getFinishReason());
            mExecutionState.markFinishedExecution();
        } catch (Throwable t) {
            mLogger.warn("Error occurred in action during the end phase. " +
                    "It is likely the action has not finished it's teardown properly");
            mExecutionState.markErrored(t);

            // try doing it again, so we can maybe make sure end runs.
            try {
                mLogger.debug("Re-Calling end for {}", this);
                mAction.end(mExecutionState.getFinishReason());
            } catch (Throwable t1) {
                // oh well. not much we can do
                mLogger.warn("Error occurred AGAIN in action during end phase. Terminating forcibly");
            }

            mExecutionState.markFinishedExecution();
        }
    }

    private boolean wasTimedOut() {
        return mExecutionState.isTimedOut();
    }

    @Override
    public String toString() {
        if (!mParent) {
            return mAction.toString();
        } else {
            //noinspection StringBufferReplaceableByString
            StringBuilder builder = new StringBuilder();
            builder.append(mAction.toString());
            builder.append(" (IN GROUP)");
            return builder.toString();
        }
    }
}
