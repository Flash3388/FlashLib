package com.flash3388.flashlib.net.messaging.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    static final int VERSION = 1;

    private final int mMessageType;

    public MessageHeader(int messageType) {
        mMessageType = messageType;
    }

    public MessageHeader(DataInput dataInput) throws IOException {
        int version = dataInput.readInt();
        if (version != VERSION) {
            throw new IOException("message header version mismatch: " + version);
        }

        mMessageType = dataInput.readInt();
    }

    public int getMessageType() {
        return mMessageType;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(VERSION);
        dataOutput.writeInt(mMessageType);
    }
}
