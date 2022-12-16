package com.flash3388.flashlib.robot.net;

import com.flash3388.flashlib.net.messaging.MessageReceiver;
import com.flash3388.flashlib.net.messaging.MessageQueue;
import com.flash3388.flashlib.net.messaging.MessageType;

/**
 * The network module which implements the <i>messaging</i> protocol.
 * This protocol implements a clients&lt;-&gt;server message-based communication.
 *
 * @since FlashLib 3.2.0
 */
public interface MessagingInterface {

    /**
     * Registers a new message type to support. If the {@link MessageType#getKey()} overlaps
     * with an existing, in use key, the old type will be overwritten with this one.
     *
     * @param type message type
     */
    void registerMessageType(MessageType type);

    /**
     * Retrieves the message queue, capable of queuing messages to be sent.
     *
     * @return message queue
     */
    MessageQueue getQueue();

    /**
     * Retrieves the message receiver, capable of receiving message.
     *
     * @return message receiver
     */
    MessageReceiver getReceiver();
}
