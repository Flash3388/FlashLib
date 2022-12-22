package com.flash3388.flashlib.net.hfcs.messages;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;

public class DataMessageType implements MessageType {

    public static final int KEY = 100001;

    private final InPackage mInPackage;

    public DataMessageType(InPackage inPackage) {
        mInPackage = inPackage;
    }

    public DataMessageType() {
        this(null);
    }

    @Override
    public int getKey() {
        return KEY;
    }

    @Override
    public Message create() {
        assert mInPackage != null;
        return new DataMessage(mInPackage);
    }
}
