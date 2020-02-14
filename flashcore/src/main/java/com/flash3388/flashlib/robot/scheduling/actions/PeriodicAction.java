package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class PeriodicAction extends ActionBase {

    private final Clock mClock;
    private final Runnable mRunnable;
    private final Time mPeriod;
    private Time mNextRun;

    public PeriodicAction(Clock clock, Runnable runnable, Time period) {
        mClock = clock;
        mRunnable = runnable;
        mPeriod = period;
    }

    public PeriodicAction(Runnable runnable, Time period) {
        this(RunningRobot.getInstance().getClock(), runnable, period);
    }

    @Override
    public void initialize() {
        mNextRun = Time.INVALID;
    }

    @Override
    public void execute() {
        if (!mNextRun.isValid() || mClock.currentTime().largerThanOrEquals(mNextRun)) {
            mRunnable.run();
            mNextRun = mClock.currentTime().add(mPeriod);
        }
    }
}
