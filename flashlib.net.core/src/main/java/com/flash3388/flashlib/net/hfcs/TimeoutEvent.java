package com.flash3388.flashlib.net.hfcs;

public class TimeoutEvent<T> extends BaseHfcsInEvent<T> {

    public TimeoutEvent(HfcsInType<T> type) {
        super(type);
    }
}
