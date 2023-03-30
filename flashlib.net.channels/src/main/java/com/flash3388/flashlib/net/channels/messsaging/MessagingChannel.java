package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.InMessage;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.OutMessage;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public interface MessagingChannel extends Closeable {

    interface UpdateHandler {
        void onNewMessage(MessageInfo info, InMessage message);
        Optional<MessageAndType> getMessageForNewClient();
    }

    void processUpdates(UpdateHandler handler) throws IOException;
    void write(MessageType type, OutMessage message) throws IOException;
}
