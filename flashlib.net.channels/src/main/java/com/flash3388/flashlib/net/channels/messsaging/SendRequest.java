package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;

public class SendRequest {

    public final MessageHeader header;
    public final Message message;
    public final boolean isOnlyForServer;

    public SendRequest(MessageHeader header, Message message) {
        this.header = header;
        this.message = message;
        this.isOnlyForServer = header.isOnlyForServer();
    }

    public SendRequest(Message message, boolean isOnlyForServer) {
        this.header = null;
        this.message = message;
        this.isOnlyForServer = isOnlyForServer;
    }
}
