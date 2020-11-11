package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.time.Time;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Scheduler is responsible for executing tasks for robots.
 * <p>
 * For more complex operations, the scheduler can use {@link Action} objects. Those objects can be added
 * to the scheduler and then executed as well. Unlike simple tasks, scheduling might depend on {@link Requirement}
 * objects for operations. The scheduler tracks the required systems of each action making sure that only one
 * {@link Action} object runs on a {@link Requirement} at any given time.
 * <p>
 * In addition, the scheduler can allow {@link Subsystem} to hold default {@link Action} objects, which
 * run only if no {@link Action} is using the {@link Subsystem} at the moment. When the scheduler runs,
 * it checks all registered {@link Requirement} objects to see if one does not have an action at the moment.
 *
 * @since FlashLib 1.0.0
 */
public interface Scheduler {

    /**
     * <p>
     *     Starts running an {@link Action}. Generally, this
     *     should be called from {@link Action#start()} implementations
     *     and not directly.
     * </p>
     *
     * @param action action to start
     *
     * @throws IllegalStateException if the action is already running on this scheduler.
     */
    void start(Action action);

    /**
     * <p>
     *     Cancels an {@link Action} being ran by the scheduler. Generally,
     *     this should be called from {@link Action#cancel()} implementations
     *     and not directly.
     * </p>
     *
     * @param action action to cancel.
     *
     * @throws IllegalStateException if the action is not running on the scheduler.
     */
    void cancel(Action action);

    /**
     * <p>
     *     Gets whether or not the given {@link Action} is running on this scheduler.
     * </p>
     *
     * @param action action to test
     *
     * @return <b>true</b> if running, <b>false</b> otherwise.
     */
    boolean isRunning(Action action);

    /**
     * <p>
     *     Gets the total time passed since the given {@link Action} started running.
     * </p>
     * <p>
     *     The action must be running.
     * </p>
     *
     * @param action action to get runtime for.
     *
     * @return {@link Time} passed since the action started running.
     *
     * @throws IllegalStateException if the action is not running.
     */
    Time getActionRunTime(Action action);

    /**
     * <p>
     *     Cancels all actions running on this scheduler if they match the
     *     given predicate as described by {@link Predicate#test(Object)} of
     *     that predicate.
     * </p>
     *
     * @param predicate {@link Predicate} determining whether or not to cancel
     *                                   an action.
     */
    void cancelActionsIf(Predicate<? super Action> predicate);

    /**
     * <p>
     *     Cancels all actions running on this scheduler.
     * </p>
     */
    void cancelAllActions();

    /**
     * <p>
     *     Sets the default {@link Action} for a given {@link Subsystem}.
     *     The given action will be started automatically when the subsystem in question
     *     has no other action running.
     * </p>
     * <p>
     *     There can only be one default action for any subsystem, thus calling this method
     *     twice on the same subsystem will overwrite the previously set default action. In addition
     *     if the previous default action is running, it will be canceled.
     * </p>
     * <p>
     *     The given subsystem must be part of the requirements of the given action.
     * </p>
     *
     * @param subsystem subsystem to set default action for.
     * @param action action to use as default
     *
     * @throws IllegalArgumentException if the given action does not require the given
     *      subsystem.
     */
    void setDefaultAction(Subsystem subsystem, Action action);

    /**
     * <p>
     *     Gets the action currently running on the given {@link Requirement requirement (or subsystem)}.
     * </p>
     *
     * @param requirement the requirement.
     *
     * @return {@link Optional} containing the action, if there is one running, or {@link Optional#empty()}
     *  if there is no action running.
     */
    Optional<Action> getActionRunningOnRequirement(Requirement requirement);

    /**
     * <p>
     *     Runs an update on the scheduler, updating the current mode used by the scheduler
     *     as well as updating internal execution information.
     * </p>
     * <p>
     *     Different implementations of the scheduler may use this method more
     *     or less often depending on the execution model of the implementation.
     *     It is recommended to invoke this method as fast as possible (~ 20ms).
     * </p>
     *
     * @param mode current mode for the scheduler.
     */
    void run(SchedulerMode mode);
}