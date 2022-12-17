package com.flash3388.flashlib.net.message;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.Remote;

import java.io.IOException;

public interface MessagingChannel {

    interface UpdateHandler {
        void onNewMessage(MessageInfo messageInfo, Message message);
        void onNewRemote(Remote remote);
    }

    void handleUpdates(UpdateHandler handler) throws IOException, InterruptedException, TimeoutException;
    void write(Message message) throws IOException, InterruptedException;
}
