package com.flash3388.flashlib.net.messaging;

import java.io.DataOutput;
import java.io.IOException;

public interface OutMessage {

    /**
     * Writes the content of the message into an output, allowing it to be sent.
     *
     * @param output output
     * @throws IOException if an I/O error occurs
     */
    void writeInto(DataOutput output) throws IOException;
}
