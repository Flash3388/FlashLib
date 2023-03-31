package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.Requirement;

import java.util.Set;


public interface ActionContext {

    void markStarted();
    void markCancelled();
    void markFinished();

    void execute();

    ActionConfiguration getConfiguration();
    boolean isFinished();

    default boolean shouldRunInDisabled() {
        return getConfiguration().getFlags().contains(ActionFlag.RUN_ON_DISABLED);
    }
    default boolean isPreferred() {
        return getConfiguration().getFlags().contains(ActionFlag.PREFERRED_FOR_REQUIREMENTS);
    }
    default Set<Requirement> getRequirements() {
        return getConfiguration().getRequirements();
    }
}
