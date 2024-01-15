package com.flash3388.flashlib.app.watchdog;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WatchdogImpl implements InternalWatchdog {

    private static final Logger LOGGER = Logging.getLogger("Watchdog");

    private final Clock mClock;
    private final String mName;
    private final Time mTimeout;
    private final FeedReporter mReporter;

    private final Lock mLock;
    private final List<TimestampReport> mReports;
    private Time mLastFeedTime;
    private boolean mDisabled;
    private boolean mIsExpired;

    public WatchdogImpl(Clock clock, String name, Time timeout, FeedReporter reporter) {
        mClock = clock;
        mName = name;
        mTimeout = timeout;
        mReporter = reporter;

        mLock = new ReentrantLock();
        mReports = new ArrayList<>(5);

        disable();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Time getTimeout() {
        return mTimeout;
    }

    @Override
    public boolean isEnabled() {
        return !mDisabled;
    }

    @Override
    public boolean isExpired() {
        // possible because boolean assignment is atomic in java memory model.
        return mIsExpired;
    }

    @Override
    public void disable() {
        mLock.lock();
        try {
            if (mDisabled) {
                throw new IllegalStateException("already disabled");
            }

            mDisabled = true;
            mIsExpired = false;
            mLastFeedTime = Time.INVALID;
            mReports.clear();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void enable() {
        mLock.lock();
        try {
            if (!mDisabled) {
                throw new IllegalStateException("already enabled");
            }

            mReports.clear();
            mIsExpired = false;
            mLastFeedTime = mClock.currentTime();
            mDisabled = false;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void reportTimestamp(String key) {
        mLock.lock();
        try {
            if (mDisabled) {
                throw new IllegalStateException("disabled");
            }

            Time now = mClock.currentTime();
            Time timePassed = now.sub(mLastFeedTime);

            mReports.add(new TimestampReport(key, timePassed));
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void feed() {
        mLock.lock();
        try {
            if (mDisabled) {
                throw new IllegalStateException("disabled");
            }

            Time now = mClock.currentTime();
            Time timePassed = now.sub(mLastFeedTime);
            if (timePassed.after(mTimeout)) {
                // feed overrun
                LOGGER.trace("Watchdog {} overrun on feed call", mName);

                Time overrun = timePassed.sub(mTimeout);
                mReporter.reportFeedExpired(mName, overrun, mReports);
            } else {
                LOGGER.trace("Watchdog {} feed", mName);
                mReporter.reportFeed(mName);
            }

            mIsExpired = false;
            mLastFeedTime = now;
            mReports.clear();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Time getTimeLeftToTimeout() {
        mLock.lock();
        try {
            Time now = mClock.currentTime();
            return now.sub(mLastFeedTime);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void checkFed() {
        mLock.lock();
        try {
            if (mDisabled) {
                // swallow as this is an inner API
                return;
            }

            Time now = mClock.currentTime();
            Time timePassed = now.sub(mLastFeedTime);
            if (timePassed.after(mTimeout)) {
                // feed overrun
                LOGGER.trace("Watchdog {} overrun on check feed", mName);

                mIsExpired = true;

                Time overrun = timePassed.sub(mTimeout);
                mReporter.reportFeedExpired(mName, overrun, mReports);
            }
        } finally {
            mLock.unlock();
        }
    }
}
