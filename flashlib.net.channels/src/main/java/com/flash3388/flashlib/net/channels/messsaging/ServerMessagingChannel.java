package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.IOException;

public interface ServerMessagingChannel extends OutMessagingChannel {

    interface UpdateHandler {
        void onClientConnected(InstanceId clientId);
        void onClientDisconnected(InstanceId clientId);
        void onNewMessage(MessageHeader header, Message message);
    }

    void processUpdates(UpdateHandler handler) throws IOException;
}
