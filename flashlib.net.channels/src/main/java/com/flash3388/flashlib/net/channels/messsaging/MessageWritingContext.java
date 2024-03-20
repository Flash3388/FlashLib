package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageWritingContext {

    private static final int INITIAL_CAPACITY = 1024;
    private static final int MAX_WRITE_BUFFERS_CAPACITY_STREAMING = 8192;
    private static final int MAX_WRITE_BUFFERS_CAPACITY_NOT_STREAMING = 1024;

    private final ChannelId mOurId;
    private final Logger mLogger;
    private final boolean mChannelStreaming;
    private final int mBuffersMaxCapacity;

    private ByteBuffer mWriteBuffer;
    private ByteBuffer mTempWriteBuffer;

    public MessageWritingContext(ChannelId ourId, Logger logger, boolean channelStreaming) {
        mOurId = ourId;
        mLogger = logger;
        mChannelStreaming = channelStreaming;
        mBuffersMaxCapacity = channelStreaming ? MAX_WRITE_BUFFERS_CAPACITY_STREAMING : MAX_WRITE_BUFFERS_CAPACITY_NOT_STREAMING;

        mWriteBuffer = ByteBuffer.allocateDirect(INITIAL_CAPACITY);
        mTempWriteBuffer = ByteBuffer.allocate(INITIAL_CAPACITY);
    }

    public MessageWritingContext(ChannelId ourId, Logger logger) {
        this(ourId, logger, true);
    }

    public void clear() {
        mTempWriteBuffer.clear();
        mWriteBuffer.clear();
    }

    public boolean writeToChannel(NetChannel channel) throws IOException {
        int wanted = mWriteBuffer.position();
        if (wanted < 1) {
            return true;
        }

        mWriteBuffer.flip();
        mWriteBuffer.rewind();

        mLogger.debug("Writing data to channel: size={}", wanted);

        int written = channel.write(mWriteBuffer);

        mWriteBuffer.compact();

        boolean finishedWriting = true;
        if (written < wanted) {
            if (!mChannelStreaming) {
                throw new IOException("not all data was written and channel is not streaming");
            }

            finishedWriting = false;
        }

        return finishedWriting;
    }

    public void update(Time sendTime, Message message, boolean onlyForServer) throws IOException {
        MessageHeader header = new MessageHeader(0, mOurId, 0, sendTime, onlyForServer);
        serialize(header, message);
    }

    public void update(MessageHeader header, Message message) throws IOException {
        serialize(header, message);
    }

    private void serialize(MessageHeader header, Message message) throws IOException {
        MessageType type = message.getType();

        mTempWriteBuffer.clear();
        ByteBufferOutputStream tempOutputStream = new ByteBufferOutputStream(mTempWriteBuffer, mBuffersMaxCapacity);
        try (DataOutputStream dataOutputStream = new DataOutputStream(tempOutputStream)) {
            type.write(message, dataOutputStream);
        }
        mTempWriteBuffer = tempOutputStream.getBuffer();
        mTempWriteBuffer.flip();

        header = new MessageHeader(
                mTempWriteBuffer.limit(),
                header.getSender(),
                type.getKey(),
                header.getSendTime(),
                header.isOnlyForServer());

        ByteBufferOutputStream outputStream = new ByteBufferOutputStream(mWriteBuffer, mBuffersMaxCapacity);
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
            outputStream.write(mTempWriteBuffer);
        } catch (IOException | RuntimeException | Error e) {
            outputStream.revert();
            throw e;
        }

        mWriteBuffer = outputStream.getBuffer();
    }
}
