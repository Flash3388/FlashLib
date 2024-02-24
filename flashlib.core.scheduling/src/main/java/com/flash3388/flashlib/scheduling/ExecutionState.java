package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

public class ExecutionState {

    private final ExecutionStatus mStatus;
    private final FinishReason mFinishReason;
    private final Time mRunTime;
    private final Time mTimeLeft;

    public ExecutionState(ExecutionStatus status, FinishReason finishReason, Time time, Time timeLeft) {
        mStatus = status;
        mFinishReason = finishReason;
        mRunTime = time;
        mTimeLeft = timeLeft;
    }

    public static ExecutionState pending() {
        return new ExecutionState(ExecutionStatus.PENDING, null, Time.INVALID, Time.INVALID);
    }

    public static ExecutionState executing(Time runTime, Time timeLeft) {
        return new ExecutionState(ExecutionStatus.EXECUTING, null, runTime, timeLeft);
    }

    public static ExecutionState finished(ExecutionStatus status, FinishReason finishReason) {
        if (status != ExecutionStatus.FINISHED && status != ExecutionStatus.CANCELLED) {
            throw new IllegalArgumentException("expected status FINISHED or CANCELLED");
        }

        return new ExecutionState(status, finishReason, Time.INVALID, Time.INVALID);
    }

    public static ExecutionState notRunning() {
        return new ExecutionState(null, null, Time.INVALID, Time.INVALID);
    }

    /**
     * Indicates that the action is currently running, i.e. it is either pending
     * execution or already executing.
     *
     * @return <b>true</b> if running, <b>false</b> otherwise.
     */
    public boolean isRunning() {
        return isPending() || isExecuting();
    }

    /**
     * Indicates that the action is currently pending execution. That is, it has not
     * started execution yet due to various conditions, but is expected to start executing.
     *
     * @return <b>true</b> if pending, <b>false</b> otherwise
     */
    public boolean isPending() {
        return mStatus != null && mStatus == ExecutionStatus.PENDING;
    }

    /**
     * Indicates that the action is currently executing.
     *
     * @return <b>true</b> if executing, <b>false</b> otherwise.
     */
    public boolean isExecuting() {
        return mStatus != null && mStatus == ExecutionStatus.EXECUTING;
    }

    /**
     * Indicates that the action has finished its run, meaning that it is no longer
     * running. This does not mean that it has actually executed, as the action could
     * have being stopped during pending.
     *
     * See {@link #getFinishReason()} for more information about why it has finished.
     *
     * @return <b>true</b> if run has finished, <b>false</b> otherwise
     */
    public boolean isFinished() {
        return mStatus != null &&
                (mStatus == ExecutionStatus.FINISHED || mStatus == ExecutionStatus.CANCELLED);
    }

    /**
     * Gets the time passed since the action associated has started running.
     *
     * @return time since start, or {@link Time#INVALID} if action is not currently running.
     */
    public Time getRunTime() {
        return mRunTime;
    }

    /**
     * Gets the time remaining until the associated action is considered as timed-out.
     * If no timeout was defined, returns {@link Time#INVALID}.
     *
     * @return time left until timeout, or {@link Time#INVALID} if timeout was not
     *      defined or action is not currently running.
     */
    public Time getTimeLeft() {
        return mTimeLeft;
    }

    /**
     * Gets the reason for which the action has stopped running.
     *
     * @return {@link FinishReason}.
     *
     * @throws IllegalStateException if the action has not finished
     * @see #isFinished()
     */
    public FinishReason getFinishReason() {
        if (!isFinished()) {
            throw new IllegalStateException("not finished, no finish reason");
        }

        return mFinishReason;
    }
}
