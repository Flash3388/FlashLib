package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

/**
 * Provides control and status information about an executing action. Can be used
 * by the action itself to query information about itself, or to control its execution.
 * <p>
 * Properties defined during the execution of this action will generally not remain after this action
 * has finished execution.
 *
 * @since FlashLib 3.2.0
 */
public interface ActionControl extends ActionPropertyAccessor {

    /**
     * Gets the configuration associated with the action.
     *
     * @return configuration
     */
    ActionConfiguration getConfiguration();

    /**
     * Gets the time passed since the action associated has started running.
     *
     * @return time since start.
     */
    Time getRunTime();

    /**
     * Gets the time remaining until the associated action is considered as timed-out.
     * If no timeout was defined, returns {@link Time#INVALID}.
     *
     * @return time left until timeout, or {@link Time#INVALID} if timeout was not defined.
     */
    Time getTimeLeft();

    /**
     * Creates a context for running actions. Can be used to manually execute actions. Should be
     * used carefully as no requirements checks are made on the action.
     * Generally used by groups to run actions.
     *
     * @param action action to use.
     * @return execution context.
     */
    ExecutionContext createExecutionContext(Action action);

    /**
     * Marks the associated action as finished.
     */
    void finish();

    /**
     * Marks the associated action as cancelled.
     */
    void cancel();
}
