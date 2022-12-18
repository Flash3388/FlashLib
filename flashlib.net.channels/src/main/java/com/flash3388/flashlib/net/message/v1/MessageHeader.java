package com.flash3388.flashlib.net.message.v1;

import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    static final int VERSION = 1;

    private final InstanceId mSenderId;
    private final int mMessageType;

    public MessageHeader(InstanceId senderId, int messageType) {
        mSenderId = senderId;
        mMessageType = messageType;
    }

    public MessageHeader(DataInput dataInput) throws IOException {
        int version = dataInput.readInt();
        if (version != VERSION) {
            throw new IOException("message header version mismatch: " + version);
        }

        mSenderId = InstanceId.createFrom(dataInput);
        mMessageType = dataInput.readInt();
    }

    public InstanceId getSenderId() {
        return mSenderId;
    }

    public int getMessageType() {
        return mMessageType;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(VERSION);
        mSenderId.writeTo(dataOutput);
        dataOutput.writeInt(mMessageType);
    }
}
