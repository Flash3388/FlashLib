package com.flash3388.flashlib.net.channels.nio;

import java.nio.channels.SelectionKey;

public interface UpdateRegistration {

    SelectionKey getKey();

    void requestConnectionUpdates();
    void requestReadUpdates();
    void requestReadWriteUpdates();
    void requestUpdate(Object param);

    void cancel();
}
