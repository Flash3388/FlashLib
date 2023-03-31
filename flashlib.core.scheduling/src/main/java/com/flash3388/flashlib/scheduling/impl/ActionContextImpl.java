package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ExecutionPhase;
import com.flash3388.flashlib.scheduling.ExecutionState;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class ActionContextImpl implements ActionContext {

    private final ActionInterface mInterface;
    private final ActionState mState;
    private final Clock mClock;
    private final Logger mLogger;

    private final ActionControl mControl;

    public ActionContextImpl(ActionInterface anInterface, ActionState state, Clock clock, Logger logger) {
        mInterface = anInterface;
        mState = state;
        mClock = clock;
        mLogger = logger;

        mControl = new ActionControlImpl(mState, clock, logger);

        mState.setState(ExecutionState.PENDING);
        mState.setPhase(ExecutionPhase.INITIALIZATION);
    }

    @Override
    public void markStarted() {
        Time now = mClock.currentTime();
        mState.markStarted(now);

        mLogger.debug("Action {} marked as started", this);
    }

    @Override
    public void markCancelled() {
        markCancelled(false);
    }

    @Override
    public void markFinished() {
        mState.markFinished();
    }

    @Override
    public void execute() {
        if (hasTimedOut()) {
            // timed out
            markCancelled(true);
        }

        if (mState.shouldFinish()) {
            finish();
            return;
        }

        switch (mState.getPhase()) {
            case INITIALIZATION:
                callInitialize();
                break;
            case EXECUTION:
                callExecute();
                break;
        }

        if (mState.shouldFinish()) {
            finish();
        }
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mState.getConfiguration();
    }

    @Override
    public boolean isFinished() {
        return mState.getState() == ExecutionState.FINISHED;
    }

    private void markCancelled(boolean timeout) {
        mState.markCancelled(timeout);
    }

    private void callInitialize() {
        try {
            mLogger.trace("Calling action.initialize");
            mInterface.initialize(mControl);
        } catch (Throwable t) {
            mLogger.error("Error while running an action", t);
            markCancelled();
        }

        mState.setPhase(ExecutionPhase.EXECUTION);
    }

    private void callExecute() {
        try {
            mLogger.trace("Calling action.execute");
            mInterface.execute(mControl);
        } catch (Throwable t) {
            mLogger.error("Error while running an action", t);
            markCancelled();
        }
    }

    private void finish() {
        mState.setPhase(ExecutionPhase.END);

        try {
            mLogger.trace("Calling action.end");
            mInterface.end(mState.getFinishReason());
        } catch (Throwable t) {
            mLogger.error("Error while running an action (in end!!!)", t);
            markCancelled();

            try {
                mLogger.trace("Calling action.end");
                mInterface.end(mState.getFinishReason());
            } catch (Throwable t1) {
                mLogger.error("Error again while running an action (in end!!!)", t1);
            }
        }

        mState.setState(ExecutionState.FINISHED);
    }

    private boolean hasTimedOut() {
        Time now = mClock.currentTime();
        Time endTime = mState.getEndTime();
        if (!endTime.isValid()) {
            return false;
        }

        return now.after(endTime);
    }

    @Override
    public String toString() {
        return String.format("%s{id=%s, name=%s}",
                mInterface.getClass().getSimpleName(),
                mState.getId(),
                mState.getConfiguration().getName());
    }
}
