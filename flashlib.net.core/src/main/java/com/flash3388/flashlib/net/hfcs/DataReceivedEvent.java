package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Event;

public class DataReceivedEvent<T> implements Event {

    private final InstanceId mSender;
    private final HfcsInType<T> mType;
    private final T mData;

    public DataReceivedEvent(InstanceId sender, HfcsInType<T> type, T data) {
        mSender = sender;
        mType = type;
        mData = data;
    }

    public InstanceId getSender() {
        return mSender;
    }

    public HfcsInType<T> getType() {
        return mType;
    }

    public T getData() {
        return mData;
    }
}
