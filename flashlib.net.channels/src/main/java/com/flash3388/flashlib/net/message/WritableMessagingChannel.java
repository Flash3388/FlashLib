package com.flash3388.flashlib.net.message;

import java.io.IOException;

public interface WritableMessagingChannel {

    void write(Message message) throws IOException, InterruptedException;
}
