package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.time.Time;

/**
 * Meta-data for a message received.
 *
 * @since FlashLib 3.2.0
 */
public interface MessageMetadata {

    /**
     * Gets the id of the sender.
     *
     * @return sender
     */
    ChannelId getSender();

    /**
     * Gets the time at which the message was received.
     *
     * @return timestamp
     */
    Time getTimestamp();

    /**
     * Gets the type of the message.
     *
     * @return type
     */
    MessageType getType();
}
