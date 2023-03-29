package com.flash3388.flashlib.net.hfcs.messages;

import com.flash3388.flashlib.net.channels.messsaging.Message;
import com.flash3388.flashlib.net.hfcs.InType;

public class HfcsInMessage implements Message {

    private final InType<?> mType;
    private final Object mData;

    public HfcsInMessage(InType<?> type, Object data) {
        mType = type;
        mData = data;
    }

    public InType<?> getType() {
        return mType;
    }

    public Object getData() {
        return mData;
    }
}
