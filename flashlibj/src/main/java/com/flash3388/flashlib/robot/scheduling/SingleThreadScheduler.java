package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class SingleThreadScheduler implements Scheduler {

    private final SubsystemControl mSubsystemControl;
    private final ActionControl mActionControl;
    private final SchedulerIteration mSchedulerIteration;

    public SingleThreadScheduler(Clock clock, Logger logger) {
        mSubsystemControl = new SubsystemControl(logger);
        mActionControl = new ActionControl(clock, mSubsystemControl);
        mSchedulerIteration = new SchedulerIteration(mActionControl, mSubsystemControl, logger);
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
    public void cancelAllActions() {
        mActionControl.stopAllActions();
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        Objects.requireNonNull(subsystem, "subsystem is null");
        Objects.requireNonNull(action, "action is null");

        mSubsystemControl.setDefaultActionOnSubsystem(subsystem, action);
    }

    @Override
    public Optional<Action> getActionRunningOnSubsystem(Subsystem subsystem) {
        Objects.requireNonNull(subsystem, "subsystem is null");
        return mSubsystemControl.getActionOnSubsystem(subsystem);
    }

    @Override
    public void run(RobotMode robotMode) {
        Objects.requireNonNull(robotMode, "robotmode is null");
        mSchedulerIteration.run(robotMode);
    }
}
