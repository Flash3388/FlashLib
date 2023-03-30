package com.flash3388.flashlib.net.obsr.impl;


import com.flash3388.flashlib.net.channels.messsaging.MessageType;
import com.flash3388.flashlib.net.channels.messsaging.OutMessage;

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
