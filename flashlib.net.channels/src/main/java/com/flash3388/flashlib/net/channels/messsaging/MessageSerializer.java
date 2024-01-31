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
    private final SerializedMessage mSerializedMessage;

    public MessageSerializer(ChannelId ourId) {
        mOurId = ourId;
        mWriteBuffer = ByteBuffer.allocateDirect(1024);
        mSerializedMessage = new SerializedMessage(mWriteBuffer);
    }

    public SerializedMessage serialize(Time sendTime, Message message, boolean onlyForServer) throws IOException {
        MessageType type = message.getType();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            type.write(message, dataOutputStream);
        }

        byte[] content = outputStream.toByteArray();
        MessageHeader header = new MessageHeader(content.length, mOurId, type.getKey(), sendTime, onlyForServer);

        outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
            dataOutputStream.write(content);
        }

        content = outputStream.toByteArray();

        mWriteBuffer.clear();
        mWriteBuffer.put(content);
        mWriteBuffer.flip();

        return mSerializedMessage;
    }

    public SerializedMessage serialize(MessageHeader header, Message message) throws IOException {
        MessageType type = message.getType();

        // todo: improve data usage when serializing and writing
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            type.write(message, dataOutputStream);
        }

        byte[] content = outputStream.toByteArray();
        header = new MessageHeader(content.length, header.getSender(), type.getKey(), header.getSendTime(), header.isOnlyForServer());

        outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
            dataOutputStream.write(content);
        }

        content = outputStream.toByteArray();

        mWriteBuffer.clear();
        mWriteBuffer.put(content);
        mWriteBuffer.flip();

        return mSerializedMessage;
    }
}
