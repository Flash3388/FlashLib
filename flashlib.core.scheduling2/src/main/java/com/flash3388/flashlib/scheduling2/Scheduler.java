package com.flash3388.flashlib.scheduling2;

import com.flash3388.flashlib.scheduling2.actions.Action;
import com.flash3388.flashlib.scheduling2.actions.ActionExecutionBuilder;
import com.flash3388.flashlib.scheduling2.actions.Status;

public interface Scheduler {

     Status start(Action action);
     ActionExecutionBuilder submit(Action action);

    /**
     * <p>
     *     Cancels all actions running on this scheduler.
     * </p>
     */
    void cancelAllActions();

    /**
     * <p>
     *     Sets the default {@link Action} for a given {@link Requirement}.
     *     The given action will be started automatically when the requirement in question
     *     has no other action running.
     * </p>
     * <p>
     *     There can only be one default action for any requirement, thus calling this method
     *     twice on the same requirement will overwrite the previously set default action. In addition
     *     if the previous default action is running, it will be canceled.
     * </p>
     * <p>
     *     The given requirement must be part of the requirements of the given action.
     * </p>
     *
     * @param requirement requirement to set default action for.
     * @param action action to use as default
     *
     * @return status of execution for the action.
     */
     Status setDefaultAction(Requirement requirement, Action action);

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
