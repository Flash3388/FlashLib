package com.flash3388.flashlib.hmi.comm;

import com.notifier.Listener;

public interface MessageListener extends Listener {

    void onNewMessage(NewMessageEvent event);
}
