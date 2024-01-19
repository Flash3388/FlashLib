package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    public static final int SIZE = Integer.BYTES * 3 + InstanceId.SIZE;

    static final int VERSION = 1;

    private final int mContentSize;
    private final InstanceId mSender;
    private final int mMessageType;
    private final Time mSendTime;

    public MessageHeader(int contentSize, InstanceId sender, int messageType, Time sendTime) {
        mContentSize = contentSize;
        mSender = sender;
        mMessageType = messageType;
        mSendTime = sendTime;
    }

    public MessageHeader(DataInput dataInput) throws IOException {
        int version = dataInput.readInt();
        if (version != VERSION) {
            throw new IOException("message header version mismatch: " + version);
        }

        mContentSize = dataInput.readInt();
        mSender = InstanceId.createFrom(dataInput);
        mMessageType = dataInput.readInt();
        // MAYBE SEND AS NANOS OR MICROS FOR BETTER ACCURACY
        mSendTime = Time.milliseconds(dataInput.readLong());
    }

    public int getContentSize() {
        return mContentSize;
    }

    public InstanceId getSender() {
        return mSender;
    }

    public int getMessageType() {
        return mMessageType;
    }

    public Time getSendTime() {
        return mSendTime;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(VERSION);
        dataOutput.writeInt(mContentSize);
        mSender.writeTo(dataOutput);
        dataOutput.writeInt(mMessageType);
        dataOutput.writeLong(mSendTime.valueAsMillis());
    }
}
