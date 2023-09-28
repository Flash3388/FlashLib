package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.annotations.MainThreadOnly;
import com.flash3388.flashlib.scheduling.triggers.Trigger;
import com.flash3388.flashlib.time.Time;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * Scheduler is the executor for FlashLib's scheduling component. It is responsible for executing all
 * running actions (as described by {@link ActionInterface}) and managing their requirements, ensuring that there's no conflict.
 * <p>
 *     In addition, the scheduler provides support for default {@link Subsystem} actions, executing them when
 *     no other actions are running for the given subsystem.
 * </p>
 *
 *
 * @since FlashLib 1.0.0
 */
public interface Scheduler {

    @MainThreadOnly
    Action createAction(ActionInterface action, ActionConfiguration configuration);

    @MainThreadOnly
    Action createAction(ActionInterface action);

    @MainThreadOnly
    ActionBuilder buildAction(ActionInterface action, ActionConfiguration configuration);

    @MainThreadOnly
    ActionBuilder buildAction(ActionInterface action);

    /**
     * <p>
     *     Cancels all actions running on this scheduler if they match the
     *     given predicate as described by {@link Predicate#test(Object)} of
     *     that predicate.
     * </p>
     * <p>
     *     It is not guaranteed that the actions will stop running immediately.
     *     This highly depends on the implementation.
     * </p>
     *
     * @param predicate {@link Predicate} determining whether or not to cancel
     *                                   an action.
     */
    @MainThreadOnly
    void cancelActionsIf(Predicate<? super ActionInterface> predicate);

    /**
     * <p>
     *     Cancels all actions running on this scheduler if they do not have the given
     *     flag configured.
     * </p>
     * <p>
     *     It is not guaranteed that the actions will stop running immediately.
     *     This highly depends on the implementation.
     * </p>
     *
     * @param flag flag, which, if missing from an action, will cause the action to be cancelled.
     */
    @MainThreadOnly
    void cancelActionsWithoutFlag(ActionFlag flag);

    /**
     * <p>
     *     Cancels all actions running on this scheduler.
     * </p>
     * <p>
     *     It is not guaranteed that the actions will stop running immediately.
     *     This highly depends on the implementation.
     * </p>
     */
    @MainThreadOnly
    void cancelAllActions();

    /**
     * <p>
     *     Sets the default {@link ActionInterface} for a given {@link Subsystem}.
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
    @MainThreadOnly
    void setDefaultAction(Subsystem subsystem, ActionInterface action);

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
    @MainThreadOnly
    Optional<ActionInterface> getActionRunningOnRequirement(Requirement requirement);

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
    @MainThreadOnly
    void run(SchedulerMode mode);

    /**
     * Creates a new trigger for registering action activation to a condition.
     *
     * @param condition when <b>true</b> marks the trigger as <em>active</em>,
     *                  when <b>false</b> marks the trigger as <em>inactive</em>.
     * @return the trigger
     */
    @MainThreadOnly
    Trigger newTrigger(BooleanSupplier condition);

    /**
     * Creates a new group for executes actions.
     *
     * @param type type of group to create. Influences the execution order and flow.
     * @return action group.
     */
    @MainThreadOnly
    ActionGroup newActionGroup(ActionGroupType type);
}