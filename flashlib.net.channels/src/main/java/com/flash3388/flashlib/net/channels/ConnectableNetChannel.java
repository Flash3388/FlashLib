package com.flash3388.flashlib.net.channels;

import java.io.IOException;
import java.net.SocketAddress;

public interface ConnectableNetChannel extends NetChannel {

    boolean startConnection(SocketAddress remote) throws IOException;
    void finishConnection() throws IOException;
}
