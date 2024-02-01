package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;

class HfcsUpdateMessage implements Message {

    private final MessageType mType;
    private final HfcsType mHfcsType;
    private final Serializable mData;

    HfcsUpdateMessage(MessageType type, HfcsType hfcsType, Serializable data) {
        mType = type;
        mHfcsType = hfcsType;
        mData = data;
    }

    @Override
    public MessageType getType() {
        return mType;
    }

    public HfcsType getHfcsType() {
        return mHfcsType;
    }

    public Serializable getData() {
        return mData;
    }
}
