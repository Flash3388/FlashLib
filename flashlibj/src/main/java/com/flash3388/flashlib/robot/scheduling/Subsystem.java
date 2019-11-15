package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.actions.Action;

import java.util.Optional;

/**
 * Subsystem is the base for robot systems. When defining a class for a system on a robot, extend this class.
 * Doing so, allows operation of the system with FlashLib's scheduling system. 
 * <p>
 * A subsystem can be defined as a system on a robot which can be used separately from other parts of the robot. 
 * Examples for subsystems include but are not limited to: drive trains, arms, shooters, etc.
 * The concept of what makes a part of a robot into a subsystem depends on the way you wish
 * to organize you code, but in general remains the same.
 * <p>
 * Each subsystem should have only one instance in our robot code.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Subsystem {

    private final Scheduler mScheduler;

    protected Subsystem(Scheduler scheduler) {
        mScheduler = scheduler;
    }

    protected Subsystem() {
        this(RunningRobot.INSTANCE.get().getScheduler());
    }

    public void setDefaultAction(Action action) {
        mScheduler.setDefaultAction(this, action);
    }

    public boolean hasCurrentAction() {
        return mScheduler.getActionRunningOnSubsystem(this).isPresent();
    }

    public void cancelCurrentAction() {
        Optional<Action> currentAction = mScheduler.getActionRunningOnSubsystem(this);
        currentAction.ifPresent(Action::cancel);
    }
}
