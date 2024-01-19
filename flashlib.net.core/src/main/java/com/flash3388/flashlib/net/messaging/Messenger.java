package com.flash3388.flashlib.net.messaging;

import com.beans.observables.RegisteredListener;
import com.castle.concurrent.service.Service;

import java.util.Collection;
import java.util.Set;

/**
 * A sender/receiver service for {@link Message} objects with other FlashLib applications.
 *
 * @since FlashLib 3.2.0
 */
public interface Messenger extends Service {

    /**
     * Register message types expected to be sent and received via
     * this messenger. Unregistered types received or sent will be ignored,
     * be sure to register all used types before use.
     *
     * @param types types to register
     */
    void registerMessageTypes(Collection<? extends MessageType> types);

    /**
     * Registers a listener for messages. Receives notifications
     * on any message type.
     *
     * @param listener listener
     * @return a registration
     */
    RegisteredListener addListener(MessageListener listener);

    /**
     * Registers a listener for messages. Receives notifications only for messages
     * of specific types.
     *
     * @param listener listener
     * @param types types of messages
     * @return a registrations
     */
    RegisteredListener addListener(MessageListener listener, Set<? extends MessageType> types);

    /**
     * Sends a message to the connected remote. If not connected, the message
     * is queued until connection is established.
     *
     * @param message message
     */
    void send(Message message);
}
