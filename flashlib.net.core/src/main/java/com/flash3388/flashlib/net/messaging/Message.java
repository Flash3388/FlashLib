package com.flash3388.flashlib.net.messaging;

/**
 * A basic message. Containing both the contents of a message and
 * serialization logic for it.
 *
 * @since FlashLib 3.2.0
 */
public interface Message {

    MessageType getType();
}
