package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Optional;
import java.util.function.Predicate;

public class EmptyScheduler implements Scheduler {


    @Override
    public void start(Action action) {

    }

    @Override
    public void cancel(Action action) {

    }

    @Override
    public boolean isRunning(Action action) {
        return false;
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
    }

    @Override
    public void cancelAllActions() {

    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {

    }

    @Override
    public Optional<Action> getActionRunningOnRequirement(Requirement requirement) {
        return Optional.empty();
    }

    @Override
    public void run(SchedulerMode mode) {

    }
}
