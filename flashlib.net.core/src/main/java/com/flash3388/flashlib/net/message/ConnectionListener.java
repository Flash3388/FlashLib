package com.flash3388.flashlib.net.message;

import com.notifier.Listener;

public interface ConnectionListener extends Listener {

    void onConnection(ConnectionEvent event);
}
