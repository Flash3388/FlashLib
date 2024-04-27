package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;

class HfcsUpdateMessage implements Message {

    private final MessageType mType;
    private final HfcsType mHfcsType;
    private final Object mData;

    HfcsUpdateMessage(MessageType type, HfcsType hfcsType, Object data) {
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

    public Object getData() {
        return mData;
    }
}
