package com.flash3388.flashlib.net.hfcs;

public class ConnectionEvent<T> extends BaseHfcsInEvent<T> {

    public ConnectionEvent(HfcsInType<T> type) {
        super(type);
    }
}
