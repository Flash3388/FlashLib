package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.time.Time;

class InDataNode {

    private final HfcsInType<?> mType;
    private final Time mTimeout;

    private boolean mIsTimedout;
    private Time mReceivedExpiration;

    InDataNode(HfcsInType<?> type, Time timeout) {
        mType = type;
        mTimeout = timeout;

        mIsTimedout = false;
        mReceivedExpiration = Time.INVALID;
    }

    public HfcsInType<?> getType() {
        return mType;
    }

    public void reset(Time now) {
        updateReceived(now);
    }

    public void updateReceived(Time now) {
        if (mTimeout.isValid()) {
            mReceivedExpiration = now.add(mTimeout);
        }

        mIsTimedout = false;
    }

    public boolean markTimedoutIfNecessary(Time now) {
        if (!mReceivedExpiration.isValid() || mIsTimedout) {
            return false;
        }

        if (now.largerThanOrEquals(mReceivedExpiration)) {
            mIsTimedout = true;
            return true;
        }

        return false;
    }
}
