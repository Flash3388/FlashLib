package com.flash3388.flashlib.net.old.messanger;

import com.castle.time.exceptions.TimeoutException;

import java.io.IOException;

public interface MessagingServerChannel extends MessagingChannel {

    void handleUpdates() throws IOException, TimeoutException, InterruptedException;
}
