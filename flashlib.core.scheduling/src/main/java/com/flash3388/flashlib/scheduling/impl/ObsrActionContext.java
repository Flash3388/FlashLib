package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredEntry;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ExecutionStatus;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;

public class ObsrActionContext {

    private final StoredObject mRootObject;
    private final boolean mDeleteOnFinish;

    private final StoredEntry mStatus;
    private final StoredEntry mPhase;
    private final StoredEntry mClass;
    private final StoredEntry mName;
    private final StoredEntry mTimeout;
    private final StoredEntry mRequirements;
    private final StoredObject mPropertiesRoot;

    private boolean mIsDeleted;

    public ObsrActionContext(StoredObject object, Action action, ActionConfiguration configuration, boolean deleteOnFinish) {
        mRootObject = object;
        mDeleteOnFinish = deleteOnFinish;

        mStatus = object.getEntry("status");
        mStatus.setString(ExecutionStatus.PENDING.name());

        mPhase = object.getEntry("phase");
        mPhase.setString(ExecutionPhase.STARTUP.name());

        mClass = object.getEntry("class");
        mClass.setString(action.getClass().getName());

        mName = object.getEntry("name");
        mName.setString(configuration.getName());

        mTimeout = object.getEntry("timeout");
        Time timeout = configuration.getTimeout();
        mTimeout.setDouble(timeout.isValid() ? timeout.valueAsSeconds() : -1);

        mRequirements = object.getEntry("requirements");
        mRequirements.setString(Arrays.toString(configuration.getRequirements().toArray(new Requirement[0])));

        mPropertiesRoot = mRootObject.getChild("properties");

        mIsDeleted = false;
    }

    public boolean isActive() {
        return !mIsDeleted;
    }

    public StoredObject getRootObject() {
        return mRootObject;
    }

    public StoredObject getPropertiesRoot() {
        return mPropertiesRoot;
    }

    public void updateStatus(ExecutionStatus status) {
        mStatus.setString(status.name());
    }

    public void updatePhase(ExecutionPhase phase) {
        mPhase.setString(phase.name());
    }

    public void finished() {
        if (mDeleteOnFinish) {
            delete();
        }
    }

    public void delete() {
        mIsDeleted = true;
        mRootObject.delete();
    }
}
