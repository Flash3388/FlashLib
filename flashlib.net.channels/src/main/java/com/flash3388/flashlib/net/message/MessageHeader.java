package com.flash3388.flashlib.net.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    static final int VERSION = 1;

    private final String mSenderId;
    private final int mMessageType;

    public MessageHeader(String senderId, int messageType) {
        mSenderId = senderId;
        mMessageType = messageType;
    }

    public MessageHeader(DataInput dataInput) throws IOException {
        int version = dataInput.readInt();
        if (version != VERSION) {
            throw new IOException("message header version mismatch: " + version);
        }

        mSenderId = dataInput.readUTF();
        mMessageType = dataInput.readInt();
    }

    public String getSenderId() {
        return mSenderId;
    }

    public int getMessageType() {
        return mMessageType;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(VERSION);
        dataOutput.writeUTF(mSenderId);
        dataOutput.writeInt(mMessageType);
    }
}
