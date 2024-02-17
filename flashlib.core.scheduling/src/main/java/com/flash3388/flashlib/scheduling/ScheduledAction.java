package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

/**
 * Represents an action scheduled for execution by the scheduler.
 * Allows retrieving information or controlling the execution of the action.
 *
 * This instance is not reusable between different scheduling of the action. It represents a specific
 * scheduling request resulted from {@link Scheduler#start(Action)}.
 *
 * @since FlashLib 3.6.0
 */
public interface ScheduledAction { //TODO: UPDATE SINCE TO RIGHT VERSION

    /**
     * Indicates that the action is currently pending execution. That is, it has not
     * started execution yet due to various conditions, but is expected to start executing.
     *
     * @return <b>true</b> if pending, <b>false</b> otherwise
     */
    boolean isPending();

    /**
     * Indicates that the actions is currently executing.
     *
     * @return <b>true</b> if executing, <b>false</b> otherwise.
     */
    boolean isExecuting();

    /**
     * Indicates that the action is currently running, i.e. it is either pending
     * execution or already executing.
     *
     * @return <b>true</b> if running, <b>false</b> otherwise.
     */
    boolean isRunning();

    /**
     * Indicates that the action has finished its run, meaning that it is no longer
     * running. This does not mean that it has actually executed, as the action could
     * have being stopped during pending.
     *
     * See {@link #getFinishReason()} for more information about why it has finished.
     *
     * @return <b>true</b> if run has finished, <b>false</b> otherwise
     */
    boolean isFinished();

    /**
     * Gets if the action has a timeout configured.
     *
     * @return <b>true</b> if timeout is configured, <b>false</b> otherwise
     * @see ActionConfiguration#getTimeout()
     */
    boolean hasTimeoutConfigured();

    /**
     * Gets a snapshot of the configuration used during the scheduling of the action.
     * This is the configuration of the action as it was during {@link Scheduler#start(Action)}.
     * It is unmodifiable.
     *
     * @return snapshot of the configuration.
     */
    ActionConfiguration getConfiguration();

    /**
     * Gets the time passed since the action associated has started running.
     *
     * @return time since start, or {@link Time#INVALID} if action is not currently running.
     */
    Time getRunTime();

    /**
     * Gets the time remaining until the associated action is considered as timed-out.
     * If no timeout was defined, returns {@link Time#INVALID}.
     *
     * @return time left until timeout, or {@link Time#INVALID} if timeout was not
     *      defined or action is not currently running.
     */
    Time getTimeLeft();

    /**
     * Gets the reason for which the action has stopped running.
     *
     * @return {@link FinishReason}.
     *
     * @throws IllegalStateException if the action has not finished
     * @see #isFinished()
     */
    FinishReason getFinishReason();

    /**
     * Requests the scheduler to cancel the instance's execution.
     * It is not guaranteed that the action will stop immediately, as it depends
     * both on the implementation of the scheduler and the current execution state.
     *
     * @throws IllegalStateException if the action is not running
     * @see Scheduler#cancel(Action)
     */
    void cancel();
}
