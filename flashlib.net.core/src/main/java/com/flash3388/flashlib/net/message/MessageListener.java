package com.flash3388.flashlib.net.message;

import com.notifier.Listener;

@FunctionalInterface
public interface MessageListener extends Listener {

    void onNewMessage(NewMessageEvent event);
}
