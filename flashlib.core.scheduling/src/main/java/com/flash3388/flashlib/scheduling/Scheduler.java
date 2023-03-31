package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.annotations.MainThreadOnly;

import java.util.function.BooleanSupplier;

public interface Scheduler {

    /**
     * Creates a new wrapper for an {@link ActionInterface} with a specific configuration.
     *
     * @param action action
     * @param configuration wanted configuration
     * @return {@link ConfiguredAction} a wrapper for controlling.
     */
    @MainThreadOnly
    ConfiguredAction newAction(ActionInterface action, ActionConfiguration configuration);

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
     * Creates a new {@link ManualTrigger}, used to activate manually rather
     * then automatically.
     *
     * @return a trigger.
     */
    @MainThreadOnly
    ManualTrigger newManualTrigger();

    /**
     * Creates a new group for executes actions.
     *
     * @param type type of group to create. Influences the execution order and flow.
     * @return action group.
     */
    @MainThreadOnly
    ActionGroup newActionGroup(ActionGroupType type, ActionConfiguration configuration);

    /**
     * Starts an action with a given configuration.
     *
     * @param action action
     * @param configuration configuration
     * @return a {@link ActionFuture} providing control and status tracking of an executing action.
     */
    @MainThreadOnly
    ActionFuture start(ActionInterface action, ActionConfiguration configuration);

    /**
     * <p>
     *     Cancels actions running on this scheduler. Iterates over all the actions and cancels those who
     *     do not have the given flag.
     * </p>
     * <p>
     *     It is not guaranteed that the actions will stop running immediately.
     *     This highly depends on the implementation.
     * </p>
     *
     * @param flag flag
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
     * @param configuration configuration for the action
     *
     * @throws IllegalArgumentException if the given action does not require the given
     *      subsystem.
     */
    @MainThreadOnly
    void setDefaultAction(Subsystem subsystem, ActionInterface action, ActionConfiguration configuration);

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
}
