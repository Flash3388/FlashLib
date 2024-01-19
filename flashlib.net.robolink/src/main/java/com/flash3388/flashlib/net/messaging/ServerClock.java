package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class ServerClock implements Clock {

    private final Clock mBaseClock;
    private long mServerOffsetMs;
    private long mRtt2;

    public ServerClock(Clock baseClock) {
        mBaseClock = baseClock;
        mServerOffsetMs = 0;
    }

    public Clock getBaseClock() {
        return mBaseClock;
    }

    @Override
    public Time currentTime() {
        Time base = mBaseClock.currentTime();
        long ms = base.valueAsMillis() + mServerOffsetMs;
        return Time.milliseconds(ms);
    }

    public void readjustOffset(Time serverSendTime, Time originalSendTime) {
        Time now = mBaseClock.currentTime();
        long rtt2 = now.sub(originalSendTime).valueAsMillis() / 2;
        if (rtt2 < mRtt2) {
            mRtt2 = rtt2;
            mServerOffsetMs = serverSendTime.valueAsMillis() + rtt2 - now.valueAsMillis();
        }
    }
}
