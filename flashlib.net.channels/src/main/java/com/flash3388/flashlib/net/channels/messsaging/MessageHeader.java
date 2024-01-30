package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.time.Time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    public static final int SIZE = 1 + Integer.BYTES * 3 + Long.BYTES + ChannelId.SIZE;

    static final int VERSION = 1;

    private final int mContentSize;
    private final ChannelId mSender;
    private final int mMessageType;
    private final Time mSendTime;
    private final boolean mOnlyForServer;

    public MessageHeader(int contentSize, ChannelId sender, int messageType, Time sendTime, boolean onlyForServer) {
        mContentSize = contentSize;
        mSender = sender;
        mMessageType = messageType;
        mSendTime = sendTime;
        mOnlyForServer = onlyForServer;
    }

    public MessageHeader(DataInput dataInput) throws IOException {
        int version = dataInput.readInt();
        if (version != VERSION) {
            throw new IOException("message header version mismatch: " + version);
        }

        mContentSize = dataInput.readInt();
        mSender = new ChannelId(dataInput);
        mMessageType = dataInput.readInt();
        // MAYBE SEND AS NANOS OR MICROS FOR BETTER ACCURACY
        mSendTime = Time.milliseconds(dataInput.readLong());
        mOnlyForServer = dataInput.readBoolean();
    }

    public int getContentSize() {
        return mContentSize;
    }

    public ChannelId getSender() {
        return mSender;
    }

    public int getMessageType() {
        return mMessageType;
    }

    public Time getSendTime() {
        return mSendTime;
    }

    public boolean isOnlyForServer() {
        return mOnlyForServer;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(VERSION);
        dataOutput.writeInt(mContentSize);
        mSender.writeInto(dataOutput);
        dataOutput.writeInt(mMessageType);
        dataOutput.writeLong(mSendTime.valueAsMillis());
        dataOutput.writeBoolean(mOnlyForServer);
    }
}
