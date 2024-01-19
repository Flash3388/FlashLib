package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;

import java.io.IOException;

public interface MessagingChannel extends OutMessagingChannel {

    interface UpdateHandler {
        void onConnect();
        void onDisconnect();
        void onNewMessage(MessageHeader header, Message message);
    }

    void processUpdates(UpdateHandler handler) throws IOException;
    void resetConnection();
}
