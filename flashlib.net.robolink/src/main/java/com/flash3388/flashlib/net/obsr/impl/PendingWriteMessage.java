package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;

public class PendingWriteMessage {

    private final MessageType mMessageType;
    private final Message mMessage;

    public PendingWriteMessage(MessageType messageType, Message message) {
        mMessageType = messageType;
        mMessage = message;
    }

    public MessageType getMessageType() {
        return mMessageType;
    }

    public Message getMessage() {
        return mMessage;
    }
}
