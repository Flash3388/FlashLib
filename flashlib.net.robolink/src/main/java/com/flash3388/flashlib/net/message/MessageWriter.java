package com.flash3388.flashlib.net.message;

import java.io.DataOutput;
import java.io.IOException;

public interface MessageWriter {

    void write(DataOutput dataOutput, MessageType type, Message message) throws IOException;
}
