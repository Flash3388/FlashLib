package com.flash3388.flashlib.net.channels.messsaging;

public class MessageAndType {

    private final MessageType mType;
    private final OutMessage mMessage;

    public MessageAndType(MessageType type, OutMessage message) {
        mType = type;
        mMessage = message;
    }

    public MessageType getType() {
        return mType;
    }

    public OutMessage getMessage() {
        return mMessage;
    }
}
