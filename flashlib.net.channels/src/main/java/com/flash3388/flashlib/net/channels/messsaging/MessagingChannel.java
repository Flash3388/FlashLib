package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;

import java.io.Closeable;

public interface MessagingChannel extends Closeable {

    interface Listener {
        void onConnect();
        void onDisconnect();
        void onNewMessage(MessageHeader header, Message message);
        void onMessageSendingFailed(Message message);
    }

    void setListener(Listener listener);
    // connection listener

    void resetConnection();

    void queue(Message message, boolean onlyForServer);
}
