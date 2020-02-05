package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class SingleThreadScheduler implements Scheduler {

    private final ActionsRepository mActionsRepository;
    private final SchedulerIteration mSchedulerIteration;

    public SingleThreadScheduler(Clock clock, Logger logger) {
        mActionsRepository = new ActionsRepository(clock, logger);
        mSchedulerIteration = new SchedulerIteration(mActionsRepository, logger);
    }

    public SingleThreadScheduler(Clock clock) {
        this(clock, Logging.stub());
    }

    @Override
    public void add(Action action) {
        Objects.requireNonNull(action, "action is null");
        mActionsRepository.addAction(action);
    }

    @Override
    public void stopAllActions() {
        mActionsRepository.removeAllActions();
    }

    @Override
    public void stopActionsIf(Predicate<? super Action> removalPredicate) {
        mActionsRepository.removeActionsIf(removalPredicate);
    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {
        Objects.requireNonNull(subsystem, "subsystem is null");
        Objects.requireNonNull(action, "action is null");

        mActionsRepository.setDefaultActionOnSubsystem(subsystem, action);
    }

    @Override
    public Optional<Action> getActionRunningOnSubsystem(Requirement requirement) {
        return mActionsRepository.getActionOnSubsystem(requirement);
    }

    @Override
    public void run(RobotMode robotMode) {
        mSchedulerIteration.run(robotMode);
    }
}
