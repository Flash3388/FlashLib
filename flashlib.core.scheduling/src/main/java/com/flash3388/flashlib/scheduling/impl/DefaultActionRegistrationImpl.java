package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.DefaultActionRegistration;
import com.flash3388.flashlib.scheduling.ScheduledAction;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;

import java.util.Objects;
import java.util.Optional;

public class DefaultActionRegistrationImpl extends ActionPropertyAccessorImpl implements DefaultActionRegistration {

    private final RegisteredDefaultAction mAction;

    public DefaultActionRegistrationImpl(RegisteredDefaultAction action) {
        super(action.getObsrActionContext());
        mAction = action;
    }

    @Override
    public boolean isRegistered() {
        return mAction.isRegistered();
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mAction.getConfiguration();
    }

    @Override
    public boolean isRunning() {
        if (!isRegistered()) {
            throw new IllegalStateException("action is no longer registered");
        }

        ScheduledAction scheduledAction = mAction.getScheduledAction();
        return scheduledAction != null && scheduledAction.isRunning();
    }

    @Override
    public Optional<ScheduledAction> getLastScheduled() {
        if (!isRegistered()) {
            throw new IllegalStateException("action is no longer registered");
        }

        return Optional.of(mAction.getScheduledAction());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultActionRegistrationImpl that = (DefaultActionRegistrationImpl) o;
        return Objects.equals(mAction, that.mAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mAction);
    }

    @Override
    public String toString() {
        return String.format("DefaultActionRegistration for %s on %s",
                mAction.getAction().toString(),
                mAction.getTargetSubsystem());
    }
}
