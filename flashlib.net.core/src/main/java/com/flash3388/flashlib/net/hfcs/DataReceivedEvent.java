package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Event;

public class DataReceivedEvent<T> implements Event {

    private final InstanceId mSender;
    private final InType<T> mType;
    private final T mData;

    public DataReceivedEvent(InstanceId sender, InType<T> type, T data) {
        mSender = sender;
        mType = type;
        mData = data;
    }

    public InstanceId getSender() {
        return mSender;
    }

    public InType<T> getType() {
        return mType;
    }

    public T getData() {
        return mData;
    }
}
