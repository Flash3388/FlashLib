package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Optional;

public interface RequirementsControl {

    void updateRequirementsNoCurrentAction(Action action);
    void updateRequirementsWithNewRunningAction(Action action, ActionContext actionContext);

    Optional<Action> getActionOnRequirement(Requirement requirement);

    void setDefaultActionOnSubsystem(Subsystem subsystem, Action action);
    Iterable<Action> getDefaultActionsToStart();
}
