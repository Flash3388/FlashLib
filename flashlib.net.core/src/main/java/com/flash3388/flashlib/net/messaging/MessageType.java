package com.flash3388.flashlib.net.messaging;

/**
 * Represents a type of message.
 * Used to transmit the message and parse it.
 *
 * Each type is represented by an integer key, which has to be unique for identification
 * across connections.
 *
 * @since FlashLib 3.2.0
 */
public interface MessageType {

    /**
     * Unique identifier of the type.
     *
     * @return key
     */
    int getKey();

    /**
     * Creates a new instance of the message type represented by this type.
     *
     * @return empty message
     */
    Message create();
}
