package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.time.Time;

class MessageMetadataImpl implements MessageMetadata {

    private final ChannelId mSender;
    private final Time mTimestamp;
    private final MessageType mType;

    MessageMetadataImpl(ChannelId sender, Time timestamp, MessageType type) {
        mSender = sender;
        mTimestamp = timestamp;
        mType = type;
    }

    @Override
    public ChannelId getSender() {
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
