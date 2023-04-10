package com.flash3388.flashlib.net.messaging;


public class PendingWriteMessage {

    private final MessageType mMessageType;
    private final OutMessage mMessage;

    public PendingWriteMessage(MessageType messageType, OutMessage message) {
        mMessageType = messageType;
        mMessage = message;
    }

    public MessageType getMessageType() {
        return mMessageType;
    }

    public OutMessage getMessage() {
        return mMessage;
    }
}
