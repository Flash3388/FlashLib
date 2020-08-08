package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class SingleThreadScheduler implements Scheduler {

    private final RequirementsControl mRequirementsControl;
    private final ActionControl mActionControl;
    private final SchedulerIteration mSchedulerIteration;

    public SingleThreadScheduler(Clock clock, Logger logger) {
        mRequirementsControl = new RequirementsControl(logger);
        mActionControl = new ActionControl(clock, mRequirementsControl);
        mSchedulerIteration = new SchedulerIteration(mActionControl, mRequirementsControl, logger);
    }

    public SingleThreadScheduler(Clock clock) {
        this(clock, Logging.stub());
    }

    @Override
    public void start(Action action) {
        Objects.requireNonNull(action, "action is null");
        mActionControl.startAction(action);
    }

    @Override
    public void cancel(Action action) {
        Objects.requireNonNull(action, "action is null");
        mActionControl.cancelAction(action);
    }

    @Override
    public boolean isRunning(Action action) {
        Objects.requireNonNull(action, "action is null");
        return mActionControl.isActionRunning(action);
    }

    @Override
    public void cancelActionsIf(Predicate<? super Action> predicate) {
        mActionControl.cancelActionsIf(predicate);
    }

    @Override
    public void cancelAllActions() {
        mActionControl.cancelAllActions();
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        Objects.requireNonNull(subsystem, "subsystem is null");
        Objects.requireNonNull(action, "action is null");

        mRequirementsControl.setDefaultActionOnSubsystem(subsystem, action);
    }

    @Override
    public Optional<Action> getActionRunningOnRequirement(Requirement requirement) {
        Objects.requireNonNull(requirement, "requirement is null");
        return mRequirementsControl.getActionOnRequirement(requirement);
    }

    @Override
    public void run(RobotMode robotMode) {
        Objects.requireNonNull(robotMode, "robotmode is null");
        mSchedulerIteration.run(robotMode);
    }
}
