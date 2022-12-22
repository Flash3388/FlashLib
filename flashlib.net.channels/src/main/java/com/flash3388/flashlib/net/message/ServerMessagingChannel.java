package com.flash3388.flashlib.net.message;

import com.castle.time.exceptions.TimeoutException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public interface ServerMessagingChannel extends WritableMessagingChannel, Closeable {

    interface UpdateHandler extends MessagingChannel.UpdateHandler {
        Optional<MessageToSend> onNewClientSend();
    }

    void handleUpdates(UpdateHandler handler) throws IOException, InterruptedException, TimeoutException;
}
