package edu.flash3388.flashlib.communications.message.event;

import edu.flash3388.flashlib.event.Listener;

@FunctionalInterface
public interface MessageListener extends Listener {

    void onMessageReceived(MessageEvent e);
}
