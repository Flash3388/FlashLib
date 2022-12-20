package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.time.Time;

public class StoredEntryNode {

    private Value mValue;
    private Time mLastChangeTimestamp;

    public StoredEntryNode() {
        mValue = new Value();
        mLastChangeTimestamp = Time.INVALID;
    }

    public Value getValue() {
        return mValue;
    }

    public Time getLastChangeTimestamp() {
        return mLastChangeTimestamp;
    }

    public void setValue(Value value, Time currentTime) {
        mValue = value;
        mLastChangeTimestamp = currentTime;
    }
}
