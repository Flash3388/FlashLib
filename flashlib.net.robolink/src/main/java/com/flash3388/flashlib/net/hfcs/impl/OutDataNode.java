package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class OutDataNode implements Delayed {

    private final Clock mClock;

    private final Type mType;
    private final Supplier<? extends OutData> mSupplier;
    private final Time mPeriod;

    private Time mNextSendTime;

    public OutDataNode(Clock clock, Type type, Supplier<? extends OutData> supplier, Time period) {
        mClock = clock;
        mType = type;
        mSupplier = supplier;
        mPeriod = period;

        updateSent();
    }

    public Type getType() {
        return mType;
    }

    public OutData getData() {
        return mSupplier.get();
    }

    public void updateSent() {
        mNextSendTime = mClock.currentTime().add(mPeriod);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        Time now = mClock.currentTime();
        if (mNextSendTime.lessThanOrEquals(now)) {
            return 0;
        }

        return mNextSendTime.sub(now).toUnit(unit).value();
    }

    @Override
    public int compareTo(Delayed o) {
        OutDataNode other = (OutDataNode) o;
        return mNextSendTime.compareTo(other.mNextSendTime);
    }
}
