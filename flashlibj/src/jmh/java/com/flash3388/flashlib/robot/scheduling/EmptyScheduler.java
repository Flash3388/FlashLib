package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;

import java.util.Optional;

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
    public void cancelAllActions() {

    }

    @Override
    public void setDefaultAction(Subsystem subsystem, Action action) {

    }

    @Override
    public Optional<Action> getActionRunningOnSubsystem(Subsystem subsystem) {
        return Optional.empty();
    }

    @Override
    public void run(RobotMode robotMode) {

    }
}
