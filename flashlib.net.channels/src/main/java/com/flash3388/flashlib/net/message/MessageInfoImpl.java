package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

public class MessageInfoImpl implements MessageInfo {

    private final InstanceId mSender;
    private final Time mTimestamp;

    public MessageInfoImpl(InstanceId sender, Time timestamp) {
        mSender = sender;
        mTimestamp = timestamp;
    }

    @Override
    public InstanceId getSender() {
        return mSender;
    }

    @Override
    public Time getTimestamp() {
        return mTimestamp;
    }
}
