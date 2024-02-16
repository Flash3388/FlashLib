package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

public class ExecutionState {

    private final boolean mIsPending;
    private final boolean mIsExecuting;
    private final Time mRunTime;
    private final Time mTimeLeft;


    public ExecutionState(boolean isPending, boolean isExecuting, Time runTime, Time timeLeft) {
        mIsPending = isPending;
        mIsExecuting = isExecuting;
        mRunTime = runTime;
        mTimeLeft = timeLeft;
    }

    public static ExecutionState pending() {
        return new ExecutionState(true, false, Time.INVALID, Time.INVALID);
    }

    public static ExecutionState executing(Time runTime, Time timeLeft) {
        return new ExecutionState(false, true, runTime, timeLeft);
    }

    public static ExecutionState notRunning() {
        return new ExecutionState(false, false, Time.INVALID, Time.INVALID);
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
        return mIsPending;
    }

    /**
     * Indicates that the actions is currently executing.
     *
     * @return <b>true</b> if executing, <b>false</b> otherwise.
     */
    public boolean isExecuting() {
        return mIsExecuting;
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
}
