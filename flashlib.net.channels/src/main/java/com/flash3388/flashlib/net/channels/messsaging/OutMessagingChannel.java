package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;

import java.io.Closeable;
import java.io.IOException;

public interface OutMessagingChannel extends Closeable {

    void write(Message message) throws IOException;
}
