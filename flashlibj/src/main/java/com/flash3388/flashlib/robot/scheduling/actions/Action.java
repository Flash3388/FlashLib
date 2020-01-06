package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.Subsystem;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An Action is something that can be executed on the robot. This can include any operation on the robot,
 * depending on what users want. When started, the action is added to the {@link Scheduler} which is responsible
 * for executing the action. To start an action, call {@link #start()}. This class is abstract.
 * <p>
 * An action has few running phases:
 * <ul>
 * 	<li> {@link #initialize()} called once at the start </li>
 * 	<li> {@link #execute()}: called repeatedly until {@link #isFinished()} returns true</li>
 * 	<li> {@link #end(boolean)}: called when the action is done</li>
 * </ul>
 * To indicate whether an action should stop running, users can override the {@link #isFinished()} method and
 * return true to stop the action, or false to keep running. To manually stop an action, {@link #cancel()} can
 * be called.
 * <p>
 * An action can have a timeout. If the time since the action started running has passed a given timeout, the
 * action is canceled, invoking a call to {@link #end(boolean) end(true)}. Set the timeout by calling {@link #configure()}.
 * <p>
 * It is possible to define dependencies for scheduling. Basically, if an action is using a {@link Subsystem} object
 * of our robot, it is necessary to insure that no other action will use the same object, so that it won't confuse
 * that system. To do that, users must explicitly call {@link #configure()} and pass a system which is
 * used (multiple systems can be required). By doing so, the {@link Scheduler} now knows to stop any other action
 * with at least one similar system requirement. If an action is running and another starts with a similar system
 * requirement, the previous action is canceled, invoking a call to {@link #end(boolean) end(true)}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Action {

	/**
	 * Called once when the action is started.
	 */
	default void initialize() {
    }

    /**
     * Called repeatedly during the execution of the action.
     */
    void execute();

	/**
	 * Returns true when the action should end.
	 * @return true when the action should end, false otherwise.
	 */
	default boolean isFinished() {
		return false;
	}

	/**
     * Called when the action ends run.
     * @param wasInterrupted <b>true</b> if the action ended was before {@link #isFinished()} returns true,
     *                      <b>false</b> otherwise.
	 */
	default void end(boolean wasInterrupted) {
    }

    /**
     * @return returns <b>false</b> if this action can run when disabled,
     *      <b>true</b>.
     */
	default boolean runWhenDisabled() {
	    return false;
    }

    ActionConfiguration getConfiguration();
    void setConfiguration(ActionConfiguration configuration);

    default ActionConfiguration.Editor configure() {
        return new ActionConfiguration.Editor(this, getConfiguration());
    }

    void start();
    void cancel();
    boolean isRunning();
}
