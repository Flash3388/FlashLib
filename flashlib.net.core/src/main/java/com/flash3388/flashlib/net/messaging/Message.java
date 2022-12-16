package com.flash3388.flashlib.net.messaging;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Message {

    MessageType getType();

    void writeInto(DataOutput output) throws IOException;
    void readFrom(DataInput input) throws IOException;
}
