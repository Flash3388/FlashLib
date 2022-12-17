package com.flash3388.flashlib.net.messaging;

import com.notifier.Listener;

public interface ConnectionListener extends Listener {

    void onConnection(ConnectionEvent event);
}
