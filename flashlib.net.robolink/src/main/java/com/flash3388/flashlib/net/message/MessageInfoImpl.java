package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

public class MessageInfoImpl implements MessageInfo {

    private final InstanceId mSender;
    private final Time mTimestamp;
    private final MessageType mType;

    public MessageInfoImpl(InstanceId sender, Time timestamp, MessageType type) {
        mSender = sender;
        mTimestamp = timestamp;
        mType = type;
    }

    @Override
    public InstanceId getSender() {
        return mSender;
    }

    @Override
    public Time getTimestamp() {
        return mTimestamp;
    }

    @Override
    public MessageType getType() {
        return mType;
    }
}
