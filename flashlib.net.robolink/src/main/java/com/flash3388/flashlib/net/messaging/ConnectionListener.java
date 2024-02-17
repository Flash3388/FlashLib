package com.flash3388.flashlib.net.messaging;

import com.notifier.Listener;

public interface ConnectionListener extends Listener {

    void onConnected(EmptyEvent event);
    void onDisconnected(EmptyEvent event);

    void onClientConnected(NewClientEvent event);
    void onClientDisconnected(NewClientEvent event);
}
