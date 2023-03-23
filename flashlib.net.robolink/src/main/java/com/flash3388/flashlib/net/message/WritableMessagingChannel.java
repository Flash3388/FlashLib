package com.flash3388.flashlib.net.message;

import java.io.IOException;

public interface WritableMessagingChannel {

    void write(MessageType type, Message message) throws IOException, InterruptedException;
}
