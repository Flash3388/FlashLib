package com.flash3388.flashlib.net.packets.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PacketHeader {

    static final int VERSION = 1;

    private final String mSenderId;
    private final int mContentType;
    private final int mContentSize;

    public PacketHeader(String senderId, int contentType, int contentSize) {
        mSenderId = senderId;
        mContentType = contentType;
        mContentSize = contentSize;
    }

    public PacketHeader(DataInput input) throws IOException {
        int version = input.readInt();
        if (version != VERSION) {
            throw new IOException("packet header version mismatch: " + version);
        }

        mSenderId = input.readUTF();
        mContentType = input.readInt();
        mContentSize = input.readInt();
    }

    public String getSenderId() {
        return mSenderId;
    }

    public int getContentType() {
        return mContentType;
    }

    public int getContentSize() {
        return mContentSize;
    }

    public void writeTo(DataOutput output) throws IOException {
        output.writeInt(VERSION);
        output.writeUTF(mSenderId);
        output.writeInt(mContentType);
        output.writeInt(mContentSize);
    }
}
