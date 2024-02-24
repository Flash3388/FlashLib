package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ScheduledAction;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;

import java.util.Objects;

public class RegisteredDefaultAction {

    private final long mId;
    private final Action mAction;
    private final ActionConfiguration mConfiguration;
    private final ObsrActionContext mObsrActionContext;
    private final Subsystem mSubsystem;

    private boolean mIsRegistered;
    private ScheduledAction mScheduledAction;

    public RegisteredDefaultAction(long id,
                                   Action action,
                                   ActionConfiguration configuration,
                                   ObsrActionContext obsrActionContext,
                                   Subsystem subsystem) {
        mId = id;
        mAction = action;
        mConfiguration = configuration;
        mObsrActionContext = obsrActionContext;
        mSubsystem = subsystem;

        mIsRegistered = true;
        mScheduledAction = null;
    }

    public long getId() {
        return mId;
    }

    public boolean isRegistered() {
        return mIsRegistered;
    }

    public Action getAction() {
        return mAction;
    }

    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    public ObsrActionContext getObsrActionContext() {
        return mObsrActionContext;
    }

    public Subsystem getTargetSubsystem() {
        return mSubsystem;
    }

    public ScheduledAction getScheduledAction() {
        return mScheduledAction;
    }

    void updateStarted(ScheduledAction scheduledAction) {
        mScheduledAction = scheduledAction;
    }

    void removed() {
        mObsrActionContext.delete();
        mScheduledAction = null;
        mIsRegistered = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredDefaultAction that = (RegisteredDefaultAction) o;
        return mId == that.mId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId);
    }
}
