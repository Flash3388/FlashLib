package com.flash3388.flashlib.net.messaging;

/**
 * The message queue is responsible for storing messages intended to be
 * sent.
 *
 * @since FlashLib 3.2.0
 */
public interface MessageQueue {

    /**
     * Queues a message to be sent. The type of the message must be known.
     * The message will not be sent immediately, but rather depending on its place
     * in the queue and the implementation.
     *
     * @param message message
     */
    void add(Message message);
}
