package com.flash3388.flashlib.net.messaging;

/**
 * The message receiver is responsible for storing and handling received messages.
 *
 * @since FlashLib 3.2.0
 */
public interface MessageReceiver {

    /**
     * Registers a listener to handle incoming messages.
     *
     * @param listener listener
     */
    void addListener(MessageListener listener);

    /**
     * Registers a listener to handle connection events.
     *
     * @param listener listener
     */
    void addListener(ConnectionListener listener);
}
