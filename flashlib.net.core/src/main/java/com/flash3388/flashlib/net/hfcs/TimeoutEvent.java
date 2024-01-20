package com.flash3388.flashlib.net.hfcs;

import com.notifier.Event;

public class TimeoutEvent<T> implements Event {

    private final HfcsInType<T> mType;

    public TimeoutEvent(HfcsInType<T> type) {
        mType = type;
    }

    public HfcsInType<T> getType() {
        return mType;
    }
}
