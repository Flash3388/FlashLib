package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class ServerClock implements Clock {

    private final Clock mBaseClock;
    private long mServerOffsetMs;
    private long mRtt2;

    private final Logger mLogger;

    public ServerClock(Clock baseClock, Logger logger) {
        mBaseClock = baseClock;
        mLogger = logger;
        mServerOffsetMs = 0;
        mRtt2 = Integer.MAX_VALUE;
    }

    public Time currentTimeUnmodified() {
        return mBaseClock.currentTime();
    }

    @Override
    public Time currentTime() {
        Time base = mBaseClock.currentTime();
        long ms = base.valueAsMillis() + mServerOffsetMs;
        return Time.milliseconds(ms);
    }

    public synchronized void readjustOffset(Time serverSendTime, Time originalSendTime) {
        Time now = mBaseClock.currentTime();
        long rtt2 = now.sub(originalSendTime).valueAsMillis() / 2;
        if (rtt2 < mRtt2) {
            mRtt2 = rtt2;
            mServerOffsetMs = serverSendTime.valueAsMillis() + rtt2 - now.valueAsMillis();

            mLogger.debug("ServerClock offset change: {}", mServerOffsetMs);
        }
    }

    public Time adjustToClientTime(Time time) {
        long fixedMs = time.valueAsMillis() - mServerOffsetMs;
        return Time.milliseconds(fixedMs);
    }
}
