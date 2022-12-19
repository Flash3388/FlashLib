package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

/**
 * Meta-data for a message received.
 *
 * @since FlashLib 3.2.0
 */
public interface MessageInfo {

    /**
     * Gets the id of the sender in the form on {@link InstanceId}
     *
     * @return sender
     */
    InstanceId getSender();

    /**
     * Gets the time at which the message was received.
     *
     * @return timestamp
     */
    Time getTimestamp();
}
