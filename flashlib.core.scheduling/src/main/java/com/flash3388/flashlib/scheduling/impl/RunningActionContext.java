package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.scheduling.actions.ActionFlag;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Set;

public class RunningActionContext {

    private final Action mAction;
    private final Action mParent;
    private final Logger mLogger;

    private final ActionConfiguration mConfiguration;
    private final ActionExecutionState mExecutionState;
    private final ActionControl mControl;

    private boolean mIsInitialized;

    public RunningActionContext(Action action,
                                Action parent,
                                StoredObject obsrRoot,
                                Clock clock,
                                Logger logger) {
        mAction = action;
        mParent = parent;
        mLogger = logger;

        ObsrActionContext obsrContext = new ObsrActionContext(obsrRoot);

        mConfiguration = new ActionConfiguration(mAction.getConfiguration());
        mExecutionState = new ActionExecutionState(mConfiguration, obsrContext, clock, logger);
        mControl = new ActionControlImpl(action, mConfiguration, mExecutionState, obsrContext, clock, logger);

        mIsInitialized = false;

        obsrContext.updateFromAction(action);
        obsrContext.updateFromConfiguration(mConfiguration);
    }

    public RunningActionContext(Action action,
                                StoredObject obsrRoot,
                                Clock clock,
                                Logger logger) {
        this(action, null, obsrRoot, clock, logger);
    }

    public Action getAction() {
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

    public Time getTimeLeft() {
        return mExecutionState.getTimeLeft();
    }

    public void markStarted() {
        mExecutionState.markStarted();
    }

    public void markForCancellation() {
        mExecutionState.markForCancellation();
    }

    public boolean iterate() {
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

    private void initialize() {
        try {
            mLogger.trace("Calling initialize for {}", this);
            mAction.initialize(mControl);
        } catch (Throwable t) {
            mExecutionState.markErrored(t);
        }

        mExecutionState.updatePhase(ExecutionPhase.EXECUTION);
    }

    private void execute() {
        try {
            mLogger.trace("Calling execute for {}", this);
            mAction.execute(mControl);
        } catch (Throwable t) {
            mExecutionState.markErrored(t);
        }
    }

    private void finish() {
        mExecutionState.updatePhase(ExecutionPhase.END);

        try {
            mLogger.trace("Calling end for {}", this);
            mAction.end(mExecutionState.getFinishReason());
            mExecutionState.markFinishedExecution();
        } catch (Throwable t) {
            mLogger.warn("Error occurred in action during the end phase. Calling again.");
            mExecutionState.markErrored(t);

            // try doing it again, so we can maybe make sure end runs.
            try {
                mLogger.debug("Re-Calling end for {}", this);
                mAction.end(mExecutionState.getFinishReason());
            } catch (Throwable t1) {
                mLogger.warn("Error occurred AGAIN in action during end phase." +
                        "It is likely the action has not finished it's teardown properly");
            }

            mExecutionState.markFinishedExecution();
        }
    }

    @Override
    public String toString() {
        if (mParent == null) {
            return mAction.toString();
        } else {
            //noinspection StringBufferReplaceableByString
            StringBuilder builder = new StringBuilder();
            builder.append(mAction.toString());
            builder.append(" (IN GROUP ");
            builder.append(mParent);
            builder.append(")");
            return builder.toString();
        }
    }
}
