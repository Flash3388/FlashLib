package com.flash3388.flashlib.hmi.comm.v1;

import com.flash3388.flashlib.hmi.comm.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    private static final int VERSION = 1;

    private final MessageType mType;
    private final int mContentSize;

    public MessageHeader(MessageType type, int contentSize) {
        mType = type;
        mContentSize = contentSize;
    }

    public MessageHeader(DataInput input) throws IOException {
        int version = input.readInt();
        if (version != VERSION) {
            throw new IOException("wrong header version: " + version);
        }

        int typeInt = input.readInt();
        if (typeInt < 0 || typeInt >= MessageType.values().length) {
            throw new IOException("unsupported message type: " + typeInt);
        }
        mType = MessageType.values()[typeInt];

        mContentSize = input.readInt();
    }

    public int getVersion() {
        return VERSION;
    }

    public MessageType getType() {
        return mType;
    }

    public int getContentSize() {
        return mContentSize;
    }

    public void writeTo(DataOutput output) throws IOException {
        output.writeInt(mVersion);
        output.writeInt(mType.ordinal());
        output.writeInt(mContentSize);
    }
}
