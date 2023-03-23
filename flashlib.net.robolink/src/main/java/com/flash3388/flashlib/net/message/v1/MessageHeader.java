package com.flash3388.flashlib.net.message.v1;

import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    static final int VERSION = 1;

    private final InstanceId mSenderId;
    private final int mMessageType;
    private final int mContentSize;

    public MessageHeader(InstanceId senderId, int messageType, int contentSize) {
        mSenderId = senderId;
        mMessageType = messageType;
        mContentSize = contentSize;
    }

    public MessageHeader(DataInput dataInput) throws IOException {
        int version = dataInput.readInt();
        if (version != VERSION) {
            throw new IOException("message header version mismatch: " + version);
        }

        mSenderId = InstanceId.createFrom(dataInput);
        mMessageType = dataInput.readInt();
        mContentSize = dataInput.readInt();
    }

    public InstanceId getSenderId() {
        return mSenderId;
    }

    public int getMessageType() {
        return mMessageType;
    }

    public int getContentSize() {
        return mContentSize;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(VERSION);
        mSenderId.writeTo(dataOutput);
        dataOutput.writeInt(mMessageType);
        dataOutput.writeInt(mContentSize);
    }
}
