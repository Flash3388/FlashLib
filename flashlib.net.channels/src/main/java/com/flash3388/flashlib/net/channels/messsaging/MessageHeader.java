package com.flash3388.flashlib.net.channels.messsaging;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    public static final int SIZE = Integer.BYTES * 2;

    static final int VERSION = 1;

    private final int mContentSize;

    public MessageHeader(int contentSize) {
        mContentSize = contentSize;
    }

    public MessageHeader(DataInput dataInput) throws IOException {
        int version = dataInput.readInt();
        if (version != VERSION) {
            throw new IOException("message header version mismatch: " + version);
        }

        mContentSize = dataInput.readInt();
    }

    public int getContentSize() {
        return mContentSize;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(VERSION);
        dataOutput.writeInt(mContentSize);
    }
}
