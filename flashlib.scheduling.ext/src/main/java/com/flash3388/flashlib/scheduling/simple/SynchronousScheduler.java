package com.flash3388.flashlib.scheduling.simple;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.ActionContext;
import com.flash3388.flashlib.scheduling.ActionStore;
import com.flash3388.flashlib.scheduling.RequirementsControl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Predicate;

public class SynchronousScheduler implements Scheduler {

    private final RequirementsControl mRequirementsControl;
    private final ActionStore mActionStore;
    private final SchedulerIteration mSchedulerIteration;

    public SynchronousScheduler(Clock clock, Logger logger) {
        mRequirementsControl = null;
        mActionStore = new SynchronousActionStore(clock, mRequirementsControl, logger);
        mSchedulerIteration = new SchedulerIteration(mActionStore);
    }

    @Override
    public void start(Action action) {
        mActionStore.add(action);
    }

    @Override
    public void cancel(Action action) {
        mActionStore.cancel(action);
    }

    @Override
    public boolean isRunning(Action action) {
        Optional<ActionContext> context = mActionStore.get(action);
        return context.isPresent();
    }

    @Override
    public Time getActionRunTime(Action action) {
        Optional<ActionContext> context = mActionStore.get(action);
        if (context.isPresent()) {
            return context.get().getRunTime();
        }

        throw new IllegalStateException("action not running");
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        mActionStore.cancelIf(predicate);
    }

    @Override
    public void cancelAllActions() {
        mActionStore.cancelAll();
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        mRequirementsControl.setDefaultActionOnSubsystem(subsystem, action);
    }

    @Override
    public Optional<Action> getActionRunningOnRequirement(Requirement requirement) {
        return mRequirementsControl.getActionOnRequirement(requirement);
    }

    @Override
    public void run(SchedulerMode mode) {
        mSchedulerIteration.run(mode);
    }
}
