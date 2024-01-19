package com.flash3388.flashlib.net.channels;

import java.net.SocketAddress;

public interface ServerUpdate {

    enum UpdateType {
        NONE,
        NEW_CLIENT,
        NEW_DATA
    }

    UpdateType getType();
    SocketAddress getClientAddress();

    void done();
}
