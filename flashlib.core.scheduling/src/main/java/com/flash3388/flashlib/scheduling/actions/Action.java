package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;

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
     * <p>
     *     Starts the action.
     * </p>
     *
     * @throws IllegalStateException if the action is already running.
     */
    void start();

    /**
     * <p>
     *     Cancels the action.
     * </p>
     *
     * @throws IllegalStateException if the action is not running.
     */
    void cancel();

    /**
     * <p>
     *     Gets whether or not the action is running.
     * </p>
     *
     * @return <b>true</b> if the action is running, <b>false</b> otherwise.
     */
    boolean isRunning();

    /**
     * <p>
     *     Gets the configuration of this action.
     * </p>
     *
     * @return {@link ActionConfiguration} for this action.
     */
    ActionConfiguration getConfiguration();

    /**
     * <p>
     *     Sets the configuration of this action.
     * </p>
     * <p>
     *      Configuration cannot be modified while the action is running.
     * </p>
     *
     * @param configuration {@link ActionConfiguration} to set.
     *
     * @throws IllegalStateException if the action is running.
     */
    void setConfiguration(ActionConfiguration configuration);

    /**
     * <p>
     *     Opens a configuration editor context, allowing to edit
     *     the current configuration.
     * </p>
     * <p>
     *      Configuration cannot be modified while the action is running.
     * </p>
     *
     * @return {@link ActionConfiguration.Editor} editor for configuration.
     *
     * @throws IllegalStateException if the action is running.
     */
    ActionConfiguration.Editor configure();

    // convenience methods for configuring

    /**
     * <p>
     *     Updates the requirements of the current action. Effectively updates
     *     the configuration of the action.
     * </p>
     *
     * @param requirements requirements to add.
     *
     * @return this
     * @see ActionConfiguration.Editor#requires(Collection)
     */
    default Action requires(Requirement... requirements) {
        getConfiguration().requires(Arrays.asList(requirements));
        return this;
    }

    /**
     * <p>
     *     Updates the timeout of the current action. Effectively updates
     *     the configuration of the action.
     * </p>
     *
     * @param timeout timeout to set for this action.
     *
     * @return this
     * @see ActionConfiguration.Editor#setTimeout(Time)
     */
    default Action withTimeout(Time timeout) {
        getConfiguration().setTimeout(timeout);
        return this;
    }

    // convenience methods for grouping
    // they reference subclasses, but that's how you decorate, so...

    /**
     * <p>
     *     Groups this actions with the given actions to run
     *     in a sequential order, such that this action runs first,
     *     and the given actions run in order of the given arguments.
     * </p>
     *
     * @param actions actions to group with this one.
     *
     * @return this
     * @see SequentialActionGroup
     */
    @SuppressWarnings("ClassReferencesSubclass")
    default ActionGroup andThen(Action... actions) {
        return new SequentialActionGroup()
                .add(this)
                .add(actions);
    }

    /**
     * <p>
     *     Groups this actions with the given actions to run
     *     in a parallel order, such that this actions runs in parallel
     *     of the given arguments.
     * </p>
     *
     * @param actions actions to group with this one.
     *
     * @return this
     * @see ParallelActionGroup
     */
    @SuppressWarnings("ClassReferencesSubclass")
    default ActionGroup alongWith(Action... actions) {
        return new ParallelActionGroup()
                .add(this)
                .add(actions);
    }

    /**
     * <p>
     *     Groups this actions with the given actions to run
     *     in a parallel order, such that this actions runs in parallel
     *     of the given arguments.
     * </p>
     * <p>
     *     Unlike normal parallel groups, <code>race</code> groups will
     *     stop when the first action in group stops, rather then waiting for
     *     all of the to finish.
     * </p>
     *
     * @param actions actions to group with this one.
     *
     * @return this
     * @see ParallelRaceActionGroup
     */
    @SuppressWarnings("ClassReferencesSubclass")
    default ActionGroup raceWith(Action... actions) {
        return new ParallelRaceActionGroup()
                .add(this)
                .add(actions);
    }
}