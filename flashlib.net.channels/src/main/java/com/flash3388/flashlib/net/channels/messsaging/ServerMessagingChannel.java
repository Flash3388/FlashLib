package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;

import java.io.Closeable;

public interface ServerMessagingChannel extends Closeable {

    interface Listener {
        void onClientConnected(ChannelId id);
        void onClientDisconnected(ChannelId id);
        void onNewMessage(MessageHeader header, Message message);
        void onMessageSendingFailed(ChannelId id, Message message);
    }

    void setListener(Listener listener);

    void queue(Message message);
}
