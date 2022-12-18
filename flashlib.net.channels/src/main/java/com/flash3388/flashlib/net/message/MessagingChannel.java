package com.flash3388.flashlib.net.message;

import com.castle.time.exceptions.TimeoutException;

import java.io.Closeable;
import java.io.IOException;

public interface MessagingChannel extends Closeable {

    interface UpdateHandler {
        void onNewMessage(MessageInfo messageInfo, Message message);
    }

    void handleUpdates(UpdateHandler handler) throws IOException, InterruptedException, TimeoutException;
    void write(Message message) throws IOException, InterruptedException;
}
