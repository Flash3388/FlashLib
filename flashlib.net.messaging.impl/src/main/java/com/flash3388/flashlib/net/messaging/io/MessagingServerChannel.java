package com.flash3388.flashlib.net.messaging.io;

import com.castle.time.exceptions.TimeoutException;

import java.io.IOException;

public interface MessagingServerChannel extends MessagingChannel {

    void handleNewConnections() throws IOException, TimeoutException, InterruptedException;
}
