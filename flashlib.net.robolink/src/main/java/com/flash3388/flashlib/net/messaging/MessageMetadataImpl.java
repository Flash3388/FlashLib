package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.messaging.MessageMetadata;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

public class MessageMetadataImpl implements MessageMetadata {

    private final InstanceId mSender;
    private final Time mTimestamp;
    private final MessageType mType;

    MessageMetadataImpl(InstanceId sender, Time timestamp, MessageType type) {
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
