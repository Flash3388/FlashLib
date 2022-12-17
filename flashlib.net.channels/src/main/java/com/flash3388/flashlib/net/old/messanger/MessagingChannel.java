package com.flash3388.flashlib.net.old.messanger;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.message.Message;

import java.io.Closeable;
import java.io.IOException;

public interface MessagingChannel extends Closeable {

    void setOnConnection(Runnable callback);

    void waitForConnection() throws IOException, TimeoutException, InterruptedException;

    void write(Message message) throws IOException, TimeoutException, InterruptedException;
    Message read() throws IOException, TimeoutException, InterruptedException;
}
