package com.flash3388.flashlib.net.messaging.io;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;

import java.io.Closeable;
import java.io.IOException;

public interface MessagingChannel extends Closeable {

    void waitForConnection() throws IOException, TimeoutException, InterruptedException;

    void write(Message message) throws IOException, TimeoutException;
    Message read() throws IOException, TimeoutException, InterruptedException;
}
