package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;

public interface MessagingChannel extends BaseMessagingChannel {

    interface Listener {
        void onConnect();
        void onDisconnect();
        void onNewMessage(MessageHeader header, Message message);
        void onMessageSendingFailed(Message message, Throwable cause);
    }

    void setListener(Listener listener);

    void resetChannel();
}
