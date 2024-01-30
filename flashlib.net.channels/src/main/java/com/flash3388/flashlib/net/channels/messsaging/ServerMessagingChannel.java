package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.Closeable;

public interface ServerMessagingChannel extends Closeable {

    interface Listener {
        void onClientConnected(InstanceId clientId);
        void onClientDisconnected(InstanceId clientId);
        void onNewMessage(MessageHeader header, Message message);
        // todo: add info on which clients failed
        void onMessageSendingFailed(Message message);
    }

    void setListener(Listener listener);

    void queue(Message message);
}
