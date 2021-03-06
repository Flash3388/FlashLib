package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class PeriodicAction extends ActionBase {

    private final Clock mClock;
    private final Runnable mRunnable;
    private final Time mPeriod;
    private Time mNextRun;

    PeriodicAction(Clock clock, Runnable runnable, Time period, Time nextRun) {
        mClock = clock;
        mRunnable = runnable;
        mPeriod = period;
        mNextRun = nextRun;
    }

    public PeriodicAction(Clock clock, Runnable runnable, Time period) {
        this(clock, runnable, period, Time.INVALID);
    }

    public PeriodicAction(Runnable runnable, Time period) {
        this(GlobalDependencies.getClock(), runnable, period);
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
