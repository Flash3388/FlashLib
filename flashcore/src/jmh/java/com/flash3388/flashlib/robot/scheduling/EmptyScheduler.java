package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;

import java.util.Optional;
import java.util.function.Predicate;

public class EmptyScheduler implements Scheduler {

    @Override
    public void add(Action action) {

    }

    @Override
    public void stopAllActions() {

    }

    @Override
    public void stopActionsIf(Predicate<? super Action> removalPredicate) {

    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {

    }

    @Override
    public Optional<Action> getActionRunningOnSubsystem(Requirement requirement) {
        return Optional.empty();
    }

    @Override
    public void run(RobotMode robotMode) {

    }
}
