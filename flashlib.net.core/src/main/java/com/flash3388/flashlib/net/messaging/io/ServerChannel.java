package com.flash3388.flashlib.net.messaging.io;

import java.io.IOException;

public interface ServerChannel {

    void handleNewConnections() throws IOException, InterruptedException;
}
