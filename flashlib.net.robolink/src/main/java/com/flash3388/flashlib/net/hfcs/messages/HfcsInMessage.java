package com.flash3388.flashlib.net.hfcs.messages;

import com.flash3388.flashlib.net.messaging.InMessage;
import com.flash3388.flashlib.net.hfcs.InType;

public class HfcsInMessage implements InMessage {

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
