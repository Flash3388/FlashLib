package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.DefaultActionRegistration;
import com.flash3388.flashlib.scheduling.ScheduledAction;

import java.util.Optional;

public class DefaultActionRegistrationImpl extends ActionPropertyAccessorImpl implements DefaultActionRegistration {

    private boolean mIsRegistered;
    private ScheduledAction mScheduledAction;

    public DefaultActionRegistrationImpl(ObsrActionContext obsrActionContext) {
        super(obsrActionContext);

        mIsRegistered = true;
        mScheduledAction = null;
    }

    @Override
    public boolean isRegistered() {
        return mIsRegistered;
    }

    @Override
    public boolean isRunning() {
        if (!mIsRegistered) {
            throw new IllegalStateException("action is no longer registered");
        }

        return mScheduledAction != null && mScheduledAction.isRunning();
    }

    @Override
    public Optional<ScheduledAction> getLastScheduled() {
        if (!mIsRegistered) {
            throw new IllegalStateException("action is no longer registered");
        }

        return Optional.of(mScheduledAction);
    }

    void updateStarted(ScheduledAction scheduledAction) {
        mScheduledAction = scheduledAction;
    }

    void removed() {
        mIsRegistered = false;
        mScheduledAction = null;
    }
}
