package com.flash3388.flashlib.net.messaging;

import com.notifier.Listener;

public interface MessageListener extends Listener {

    void onNewMessage(NewMessageEvent event);
}
