package com.flash3388.flashlib.scheduling2.imp;

import com.flash3388.flashlib.scheduling2.Action;
import com.flash3388.flashlib.scheduling2.Configuration;
import com.flash3388.flashlib.scheduling2.Control;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class ActionContext {

    private final Clock mClock;
    private final Action mAction;
    private final ActionState mState;
    private final Control mControl;

    private Configuration mConfiguration;

    private boolean mIsInitialized;

    public ActionContext(Clock clock,Action action, ActionState state) {
        mClock = clock;
        mAction = action;
        mState = state;

        mControl = new ControlImpl(mState, this);

        mIsInitialized = false;
    }

    public Configuration configure(Configuration originalConfiguration) {
        try {
            mAction.configure(originalConfiguration);
        } catch (Throwable t) {
            mState.markErrored(t);
            mState.markFinished(); // no need to call end because we haven't been configured even
            mConfiguration = null;
            return null;
        }

        // be wary about the user trying to hijack the configuration for later stages
        mConfiguration = new ConfigurationImpl();
        originalConfiguration.copyTo(mConfiguration);

        return mConfiguration;
    }

    public boolean iterate() {
        if (wasTimedout()) {
            mState.markCanceled();
        }

        // for finishing between iterations (maybe due to cancellation)
        if (mState.getFinishReason() != null) {
            finish();
            return true;
        }

        try {
            if (!mIsInitialized) {
                mAction.initialize(mControl);

                mIsInitialized = true;
                mState.markRunning();
            } else {
                mAction.execute(mControl);
            }
        } catch (Throwable t) {
            mState.markErrored(t);
        }

        // for finishing during iteration
        if (mState.getFinishReason() != null) {
            finish();
            return true;
        }

        return false;
    }

    public void finish() {
        try {
            mAction.end(mControl);
            mState.markFinished();
        } catch (Throwable t) {
            if (mState.getFinishReason() == ActionState.FinishReason.INTERRUPT ||
                    mState.getFinishReason() == ActionState.FinishReason.ERROR) {
                mState.markErrored(t);
            }
        }
    }

    public void markForCancellation() {
        mState.markCanceled();
    }

    public void markForFinish() {
        mState.markDone();
    }

    private boolean wasTimedout() {
        Time startTime = mState.getStartTime();
        if (!startTime.isValid()) {
            return false;
        }

        Time timeout = mConfiguration.getTimeout();
        if (!timeout.isValid()) {
            return false;
        }

        return mClock.currentTime().largerThan(startTime.add(timeout));
    }
}
