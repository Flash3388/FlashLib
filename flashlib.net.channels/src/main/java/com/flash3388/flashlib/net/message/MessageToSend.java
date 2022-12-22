package com.flash3388.flashlib.net.message;

public class MessageToSend {

    private final MessageType mType;
    private final Message mMessage;

    public MessageToSend(MessageType type, Message message) {
        mType = type;
        mMessage = message;
    }

    public MessageType getType() {
        return mType;
    }

    public Message getMessage() {
        return mMessage;
    }
}
