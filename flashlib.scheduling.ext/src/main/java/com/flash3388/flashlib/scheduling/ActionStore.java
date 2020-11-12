package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public interface ActionStore {

    void add(Action action);

    void cancel(Action action);
    void cancelIf(Predicate<? super Action> predicate);
    void cancelAll();

    Optional<ActionContext> get(Action action);

    Optional<Action> getActionOnRequirement(Requirement requirement);
    void setDefaultActionOnSubsystem(Subsystem subsystem, Action action);

    Map<Action, ActionContext> updateActionStatus(Collection<Action> stoppedActions, SchedulerMode mode);
}
