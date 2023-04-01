package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class InDataNode {

    private final InType<?> mType;
    private final Time mReceiveTimeout;
    private final Logger mLogger;

    private final AtomicReference<Time> mReceiveAtTimeout;
    private final AtomicBoolean mHasTimedOut;

    public InDataNode(InType<?> type, Time receiveTimeout, Logger logger) {
        mType = type;
        mReceiveTimeout = receiveTimeout;
        mLogger = logger;

        mReceiveAtTimeout = new AtomicReference<>(Time.INVALID);
        mHasTimedOut = new AtomicBoolean(false);
    }

    public InType<?> getType() {
        return mType;
    }

    public void updateReceived(Time now) {
        mLogger.debug("In type {} received data", mType.getKey());
        if (mReceiveTimeout.isValid()) {
            mReceiveAtTimeout.set(now.add(mReceiveTimeout));
        }

        mHasTimedOut.set(false);
    }

    public boolean hasReceiveTimedOut(Time now) {
        if (mHasTimedOut.get()) {
            return false;
        }

        Time receiveAtTimeout = mReceiveAtTimeout.get();
        if (!receiveAtTimeout.isValid()) {
            return false;
        }

        return now.after(receiveAtTimeout);
    }

    public void markTimedOut() {
        mLogger.warn("Type {} has reached timeout", mType.getKey());
        mReceiveAtTimeout.set(Time.INVALID);
        mHasTimedOut.set(true);
    }
}
