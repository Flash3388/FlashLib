package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.scheduling.actions.ActionFlag;
import com.flash3388.flashlib.scheduling.actions.ActionGroup;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Set;

public class RunningActionContext {

    private final Action mAction;
    private final ActionGroup mParent;
    private final ObsrActionContext mObsrContext;
    private final Logger mLogger;

    private final ActionConfiguration mConfiguration;

    private boolean mIsInitialized;
    private boolean mIsCanceled;
    private Time mStartTime;
    private Time mEndTime;

    public RunningActionContext(Action action, ActionGroup parent, ObsrActionContext obsrContext, Logger logger) {
        mAction = action;
        mParent = parent;
        mObsrContext = obsrContext;
        mLogger = logger;

        mConfiguration = new ActionConfiguration(mAction.getConfiguration());
        mIsInitialized = false;
        mIsCanceled = false;
        mStartTime = Time.INVALID;
        mEndTime = Time.INVALID;

        mObsrContext.updateFromAction(action);
        mObsrContext.updateFromConfiguration(mConfiguration);
    }

    public RunningActionContext(Action action, Logger logger, ObsrActionContext obsrContext) {
        this(action, null, obsrContext, logger);
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

    public Time getStartTime() {
        return mStartTime;
    }

    public void markStarted(Time now) {
        mStartTime = now;
        if (mConfiguration.getTimeout().isValid()) {
            mEndTime = now.add(mConfiguration.getTimeout());
        }

        mObsrContext.updateStatus(ExecutionStatus.RUNNING);
        mObsrContext.updatePhase(ExecutionPhase.INITIALIZATION);
    }

    public void markForCancellation() {
        mIsCanceled = true;
        mObsrContext.updateStatus(ExecutionStatus.CANCELLED);
    }

    public boolean iterate(Time now) {
        if (wasTimedOut(now)) {
            markForCancellation();
        }

        if (mIsCanceled) {
            finish();
            return true;
        }

        if (!mIsInitialized) {
            initialize();
            mIsInitialized = true;
        } else {
            execute();

            if (mIsCanceled || isFinished()) {
                finish();
                return true;
            }
        }

        if (mIsCanceled) {
            finish();
            return true;
        }

        return false;
    }

    private void initialize() {
        try {
            mAction.initialize();
        } catch (Throwable t) {
            mLogger.error("Error while running an action", t);
            markForCancellation();
        }

        mObsrContext.updatePhase(ExecutionPhase.EXECUTION);
    }

    private void execute() {
        try {
            mAction.execute();
        } catch (Throwable t) {
            mLogger.error("Error while running an action", t);
            markForCancellation();
        }
    }

    private boolean isFinished() {
        try {
            return mAction.isFinished();
        } catch (Throwable t) {
            mLogger.error("Error while running an action", t);
            markForCancellation();
            return false;
        }
    }

    private void finish() {
        mObsrContext.updatePhase(ExecutionPhase.END);

        try {
            mAction.end(mIsCanceled);
            mObsrContext.updateStatus(ExecutionStatus.FINISHED);
            mObsrContext.finished();
        } catch (Throwable t) {
            mLogger.error("Error while running an action (in end!!!)", t);
            markForCancellation();
        }
    }

    private boolean wasTimedOut(Time now) {
        if (!mEndTime.isValid()) {
            return false;
        }

        return now.largerThanOrEquals(mEndTime);
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
