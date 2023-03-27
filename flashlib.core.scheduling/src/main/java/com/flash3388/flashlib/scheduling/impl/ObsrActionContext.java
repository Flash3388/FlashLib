package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredEntry;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;

public class ObsrActionContext {

    private final StoredEntry mStatus;
    private final StoredEntry mPhase;
    private final StoredEntry mName;
    private final StoredEntry mTimeout;
    private final StoredEntry mRequirements;

    public ObsrActionContext(StoredObject object) {
        mStatus = object.getEntry("status");
        mStatus.setString(ExecutionStatus.PENDING.name());

        mPhase = object.getEntry("phase");
        mPhase.setString(ExecutionPhase.STARTUP.name());

        mName = object.getEntry("name");
        mName.setString("");

        mTimeout = object.getEntry("timeout");
        mTimeout.setDouble(-1);

        mRequirements = object.getEntry("requirements");
        mRequirements.setString("");
    }

    public void updateFromConfiguration(ActionConfiguration configuration) {
        mName.setString(configuration.getName());

        Time timeout = configuration.getTimeout();
        mTimeout.setDouble(timeout.isValid() ? timeout.valueAsSeconds() : -1);

        mRequirements.setString(Arrays.toString(configuration.getRequirements().toArray(new Requirement[0])));
    }

    public void updateStatus(ExecutionStatus status) {
        mStatus.setString(status.name());
    }

    public void updatePhase(ExecutionPhase phase) {
        mPhase.setString(phase.name());
    }
}
