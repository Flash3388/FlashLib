package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.util.unique.InstanceId;

public class DataReceivedEvent<T> extends BaseHfcsInEvent<T> {

    private final InstanceId mSender;
    private final T mData;

    public DataReceivedEvent(InstanceId sender, HfcsInType<T> type, T data) {
        super(type);
        mSender = sender;
        mData = data;
    }

    public InstanceId getSender() {
        return mSender;
    }

    public T getData() {
        return mData;
    }
}
