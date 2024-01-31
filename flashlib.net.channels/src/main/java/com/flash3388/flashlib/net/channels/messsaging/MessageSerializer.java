package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Time;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageSerializer {

    public static class SerializedMessage {

        private final ByteBuffer mBuffer;

        public SerializedMessage(ByteBuffer buffer) {
            mBuffer = buffer;
        }

        public void writeInto(NetChannel channel) throws IOException {
            mBuffer.rewind();
            channel.write(mBuffer);
        }

        public int getSize() {
            return mBuffer.limit();
        }
    }

    private final ChannelId mOurId;
    private final ByteBuffer mWriteBuffer;
    private final ByteBuffer mTempWriteBuffer;
    private final SerializedMessage mSerializedMessage;

    public MessageSerializer(ChannelId ourId) {
        mOurId = ourId;
        mWriteBuffer = ByteBuffer.allocateDirect(1024);
        mTempWriteBuffer = ByteBuffer.allocate(1024);
        mSerializedMessage = new SerializedMessage(mWriteBuffer);
    }

    public SerializedMessage serialize(Time sendTime, Message message, boolean onlyForServer) throws IOException {
        MessageType type = message.getType();

        mTempWriteBuffer.clear();
        ByteBufferOutputStream tempOutputStream = new ByteBufferOutputStream(mTempWriteBuffer);
        try (DataOutputStream dataOutputStream = new DataOutputStream(tempOutputStream)) {
            type.write(message, dataOutputStream);
        }
        mTempWriteBuffer.flip();

        MessageHeader header = new MessageHeader(mTempWriteBuffer.limit(), mOurId, type.getKey(), sendTime, onlyForServer);

        mWriteBuffer.clear();

        ByteBufferOutputStream outputStream = new ByteBufferOutputStream(mWriteBuffer);
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
        }

        mWriteBuffer.put(mTempWriteBuffer);
        mWriteBuffer.flip();

        return mSerializedMessage;
    }

    public SerializedMessage serialize(MessageHeader header, Message message) throws IOException {
        MessageType type = message.getType();

        mTempWriteBuffer.clear();
        ByteBufferOutputStream tempOutputStream = new ByteBufferOutputStream(mTempWriteBuffer);
        try (DataOutputStream dataOutputStream = new DataOutputStream(tempOutputStream)) {
            type.write(message, dataOutputStream);
        }
        mTempWriteBuffer.flip();

        header = new MessageHeader(mTempWriteBuffer.limit(), header.getSender(), type.getKey(), header.getSendTime(), header.isOnlyForServer());

        mWriteBuffer.clear();
        ByteBufferOutputStream outputStream = new ByteBufferOutputStream(mWriteBuffer);
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
        }

        mWriteBuffer.put(mTempWriteBuffer);
        mWriteBuffer.flip();

        return mSerializedMessage;
    }
}
