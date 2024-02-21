package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.scheduling.actions.ActionFlag;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Set;

public class RunningActionContext {

    private final long mId;
    private final Action mAction;
    private final long mParentId;
    private final Action mParent;
    private final Logger mLogger;

    private final ActionConfiguration mConfiguration;
    private final ActionExecutionState mExecutionState;
    private final ActionControl mControl;

    private boolean mIsInitialized;

    public RunningActionContext(long id,
                                Action action,
                                long parentId,
                                Action parent,
                                ActionConfiguration configuration,
                                ObsrActionContext obsrActionContext,
                                Clock clock,
                                Logger logger) {
        mId = id;
        mAction = action;
        mParentId = parentId;
        mParent = parent;
        mConfiguration = configuration;
        mLogger = logger;

        mExecutionState = new ActionExecutionState(mConfiguration, obsrActionContext, clock, logger);
        mControl = new ActionControlImpl(id, action, mConfiguration, mExecutionState, obsrActionContext, clock, logger);

        mIsInitialized = false;
    }

    public RunningActionContext(long id,
                                Action action,
                                ActionConfiguration configuration,
                                ObsrActionContext obsrActionContext,
                                Clock clock,
                                Logger logger) {
        this(id, action, -1, null, configuration, obsrActionContext, clock, logger);
    }

    public Action getAction() {
        return mAction;
    }

    public ActionConfiguration getConfiguration() {
        return mConfiguration;
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

    public ExecutionStatus getStatus() {
        return mExecutionState.getStatus();
    }

    public FinishReason getFinishReason() {
        return mExecutionState.getFinishReason();
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

    public boolean isMarkedForEnd() {
        return mExecutionState.isMarkedForEnd();
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

        mExecutionState.markInExecution();
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
        mExecutionState.markInEnd();
        if (!mIsInitialized) {
            // did not initialize and as such end should not be called
            mLogger.debug("{} was marked for end before initializing, not calling end", this);
            mExecutionState.markFinishedExecution();
            return;
        }

        try {
            FinishReason finishReason = mExecutionState.getFinishReason();
            mLogger.trace("Calling end for {} with reason={}", this, finishReason);
            mAction.end(finishReason);
            mExecutionState.markFinishedExecution();
        } catch (Throwable t) {
            mLogger.warn("Error occurred in action during the end phase. Calling again.");
            mExecutionState.markErrored(t);

            // try doing it again, so we can maybe make sure end runs.
            try {
                FinishReason finishReason = mExecutionState.getFinishReason();
                mLogger.debug("Re-Calling end for {} with reason={}", this, finishReason);
                mAction.end(finishReason);
            } catch (Throwable t1) {
                mLogger.warn("Error occurred AGAIN in action during end phase." +
                        "It is likely the action has not finished it's teardown properly");
            }

            mExecutionState.markFinishedExecution();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mAction.getClass().getSimpleName());
        builder.append('{');
        builder.append("scheduleId=");
        builder.append(mId);
        builder.append(", name=");
        builder.append(mConfiguration.getName());
        builder.append('}');

        if (mParent != null) {
            builder.append(" (IN GROUP ");
            builder.append(mParent.getClass().getSimpleName());
            builder.append('{');
            builder.append("scheduleId=");
            builder.append(mParentId);
            builder.append('}');
            builder.append(')');
        }

        return builder.toString();
    }
}
