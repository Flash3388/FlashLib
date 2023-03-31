package com.flash3388.flashlib.net.messaging;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Writable portion of a message.
 *
 * @since FlashLib 3.2.0
 */
public interface OutMessage {

    /**
     * Writes the content of the message into an output, allowing it to be sent.
     *
     * @param output output
     * @throws IOException if an I/O error occurs
     */
    void writeInto(DataOutput output) throws IOException;
}
