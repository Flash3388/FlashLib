package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.time.Time;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

class OutDataNode implements Delayed {

    private final HfcsType mType;
    private final Supplier<? extends Serializable> mData;
    private final Time mPeriod;

    private long mNextSendTimeMs;

    OutDataNode(HfcsType type, Supplier<? extends Serializable> data, Time period) {
        mType = type;
        mData = data;
        mPeriod = period;

        mNextSendTimeMs = 0;
    }

    public HfcsType getType() {
        return mType;
    }

    public Serializable getNewData() {
        return mData.get();
    }

    public void reset() {
        mNextSendTimeMs = System.currentTimeMillis();
    }

    public void updateSent() {
        mNextSendTimeMs = System.currentTimeMillis() + mPeriod.valueAsMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        if (mNextSendTimeMs <= 0) {
            return Long.MAX_VALUE;
        }

        long diffMs = mNextSendTimeMs - System.currentTimeMillis();
        return unit.convert(diffMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long diffMillis = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        diffMillis = Math.min(diffMillis, 1);
        diffMillis = Math.max(diffMillis, -1);

        return (int) diffMillis;
    }
}
