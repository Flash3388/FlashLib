package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;

/**
 * Represents an execution context for the action, basically control the execution of the action.
 *
 * @since FlashLib 3.2.0
 */
public interface ExecutionContext {

    enum ExecutionResult {
        STILL_RUNNING,
        FINISHED
    }

    /**
     * Gets the action associated with this execution context.
     * This action is the one being executed by this context.
     *
     * @return action associated
     */
    Action getAction();

    /**
     * Gets a snapshot of the configuration used during the scheduling of the action.
     * This is the configuration of the action as it was during {@link Scheduler#start(Action)}.
     * It is unmodifiable.
     *
     * @return snapshot of the configuration.
     */
    ActionConfiguration getConfiguration();

    /**
     * Retrieves the current {@link ExecutionState} of the action. This indicates the
     * state of execution of the action and provides several details as to the running or finishing
     * status of the action.
     *
     * @return current state
     */
    ExecutionState getState();

    /**
     * Starts the execution. Must be called before calling {@link #execute(SchedulerMode)}.
     * This does not actually execute the action, but prepares the context for starting execution.
     */
    void start();

    /**
     * <p>
     *     Runs an execution iteration of the action. The actual operation of this, changes
     *     according to the current execution phase. But generally, this invokes the associated
     *     action according to the current execution phase.
     * </p>
     * <p>
     *     {@link #start()} must be called before this is used.
     * </p>
     * <p>
     *     If <em>mode</em> is not <b>null</b>, the current scheduler mode is evaluated
     *     against the configuration of the action. If {@link SchedulerMode#isDisabled()} and
     *     the actions is not configured with {@link com.flash3388.flashlib.scheduling.actions.ActionFlag#RUN_ON_DISABLED},
     *     then the action is interrupted (via {@link #interrupt()} and this returns {@link ExecutionResult#FINISHED}.
     * </p>
     *
     * @param mode current scheduler mode, or <em>null</em> if unknown.
     * @return result of the execution iteration, {@link ExecutionResult#STILL_RUNNING} indicates that the
     *      action has not finished its execution and this should still be called. {@link ExecutionResult#FINISHED}
     *      indicates that the action has finished running and this should no longer be called.
     * @throws IllegalStateException if {@link #start()} wasn't called, or the execution has finished
     */
    ExecutionResult execute(SchedulerMode mode);

    /**
     * Calls {@link #execute(SchedulerMode)} with null <em>mode</em>. Call
     * this if the mode is unknown as a shortcut.
     *
     * @return result of execution iteration.
     * @see #execute(SchedulerMode)
     */
    ExecutionResult execute();

    /**
     * Marks the action as interrupted. Does not actually finish the action execution,
     * just marks the context as needing to end. The finish reason for such a finish
     * is {@link FinishReason#CANCELED}.
     *
     * The action will actually finish on the next call to {@link #execute(SchedulerMode)}.
     */
    void markInterrupted();

    /**
     * Interrupts the execution of the action, and finishes the execution. The action will be normally
     * finished with {@link FinishReason#CANCELED}. If the action was already marked for finish,
     * the finishing of this action would not necessarily be as expected, but rather according to the
     * original finish reason.
     *
     * After this is called, the {@link #execute(SchedulerMode)} should not be called.
     *
     * @throws IllegalStateException if the execution has finished.
     */
    void interrupt();
}
