package com.flash3388.flashlib.net.messaging;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A basic message that can be sent and received through connections.
 *
 * @since FlashLib 3.2.0
 */
public interface Message {

    /**
     * The type of the message.
     *
     * @return type.
     */
    MessageType getType();

    /**
     * Writes the content of the message into an output, allowing it to be sent.
     *
     * @param output output
     * @throws IOException if an I/O error occurs
     */
    void writeInto(DataOutput output) throws IOException;

    /**
     * Reads the contents of the message.
     *
     * @param input inputs
     * @throws IOException if an I/O error occurs
     */
    void readFrom(DataInput input) throws IOException;
}
