package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;

import java.io.Closeable;

public interface BaseMessagingChannel extends Closeable {

    void start();
    void queue(Message message);
}
