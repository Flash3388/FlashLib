package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.time.Time;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class EmptyScheduler implements Scheduler {


    @Override
    public void start(ActionInterface action) {

    }

    @Override
    public void cancel(ActionInterface action) {

    }

    @Override
    public boolean isRunning(ActionInterface action) {
        return false;
    }

    @Override
    public Time getActionRunTime(ActionInterface action) {
        return null;
    }

    @Override
    public void cancelActionsIf(Predicate<? super ActionInterface> predicate) {
    }

    @Override
    public void cancelAllActions() {

    }

    @Override
    public void setDefaultAction(Subsystem subsystem, ActionInterface action) {

    }

    @Override
    public Optional<ActionInterface> getActionRunningOnRequirement(Requirement requirement) {
        return Optional.empty();
    }

    @Override
    public void run(SchedulerMode mode) {

    }

    @Override
    public Trigger newTrigger(BooleanSupplier condition) {
        return null;
    }
}
