package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
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
 * 	<li> {@link #initialize(ActionControl)} called once at the start </li>
 * 	<li> {@link #execute(ActionControl)}: called repeatedly until
 * 	{@link ActionControl#finish()} or {@link ActionControl#cancel()} are called</li>
 * 	<li> {@link #end(FinishReason)}: called when the action is done</li>
 * </ul>
 * To indicate whether an action should stop running, users can use {@link ActionControl} and
 * invoke its various methods to stop the action. To manually stop an action, {@link #cancel()} can be called.
 * <p>
 * An action can have a timeout. If the time since the action started running has passed a given timeout, the
 * action is canceled, invoking a call to {@link #end(FinishReason) end(FinishReason.TIMEDOUT)}.
 * Set the timeout by calling {@link #configure()}.
 * <p>
 * It is possible to define dependencies for scheduling. Basically, if an action is using a {@link Subsystem} object
 * of our robot, it is necessary to insure that no other action will use the same object, so that it won't confuse
 * that system. To do that, users must explicitly call {@link #configure()} and pass a system which is
 * used (multiple systems can be required). By doing so, the {@link Scheduler} now knows to stop any other action
 * with at least one similar system requirement. If an action is running and another starts with a similar system
 * requirement, the previous action is canceled, invoking a call to {@link #end(FinishReason) end(FinishReason.CANCELLED)}.
 *
 * @since FlashLib 1.0.0
 */
public interface Action {

    /**
     * Called once when the action is started.
     * @param control component for controlling and querying the action execution state.
     */
    default void initialize(ActionControl control) {
    }

    /**
     * Called repeatedly during the execution of the action.
     * @param control component for controlling and querying the action execution state.
     */
    void execute(ActionControl control);

    /**
     * Called when the action ends run.
     * @param reason reason for execution finish
     */
    default void end(FinishReason reason) {
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

    default Action flags(ActionFlag... flags) {
        getConfiguration().addFlags(flags);
        return this;
    }

    // convenience methods for grouping

    /**
     * <p>
     *     Groups this actions with the given actions to run
     *     in a sequential order, such that this action runs first,
     *     and the given actions run in order of the given arguments.
     * </p>
     *
     * @param actions actions to group with this one.
     *
     * @return {@link ActionGroup} running in sequence this and the given actions.
     */
    default ActionGroup andThen(Action... actions) {
        return Actions.sequential(this)
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
     * @return {@link ActionGroup} running in parallel this and the given actions.
     */
    default ActionGroup alongWith(Action... actions) {
        return Actions.parallel(this)
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
     * @return {@link ActionGroup} running in parallel this and the given actions.
     */
    default ActionGroup raceWith(Action... actions) {
        return Actions.race(this)
                .add(actions);
    }
}