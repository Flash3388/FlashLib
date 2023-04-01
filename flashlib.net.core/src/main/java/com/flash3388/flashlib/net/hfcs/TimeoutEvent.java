package com.flash3388.flashlib.net.hfcs;

import com.notifier.Event;

public class TimeoutEvent<T> implements Event {

    private final InType<T> mType;

    public TimeoutEvent(InType<T> type) {
        mType = type;
    }

    public InType<T> getType() {
        return mType;
    }
}
