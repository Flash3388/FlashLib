package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.impl.BufferedReader;
import com.flash3388.flashlib.net.impl.ReadableChannel;
import com.flash3388.flashlib.net.impl.TcpServerChannel;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.io.MessageSerializer;
import com.flash3388.flashlib.net.messaging.io.MessagingServerChannel;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerMessagingChannel implements MessagingServerChannel {

    private final TcpServerChannel mChannel;
    private final MessageSerializer mSerializer;
    private final BlockingQueue<Message> mReceivedMessages;
    private final UpdateHandler mUpdateHandler;

    public ServerMessagingChannel(SocketAddress bindAddress, KnownMessageTypes messageTypes, Logger logger) {
        mChannel = new TcpServerChannel(bindAddress, logger);
        mSerializer = new MessageSerializer(messageTypes);
        mReceivedMessages = new LinkedBlockingQueue<>();

        ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
        mUpdateHandler = new UpdateHandler(mSerializer, readBuffer, mReceivedMessages);
    }

    @Override
    public void handleUpdates() throws IOException, TimeoutException, InterruptedException {
        mChannel.handleUpdates(mUpdateHandler);
    }

    @Override
    public void waitForConnection() throws IOException, TimeoutException, InterruptedException {
        mChannel.waitUntilHasClients();
    }

    @Override
    public void write(Message message) throws IOException, TimeoutException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            mSerializer.write(dataOutputStream, message);
            dataOutputStream.flush();

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
            mChannel.writeToAll(buffer);
        }
    }

    @Override
    public Message read() throws IOException, TimeoutException, InterruptedException {
        return mReceivedMessages.take();
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }

    private static class UpdateHandler implements TcpServerChannel.UpdateHandler {

        private final MessageSerializer mSerializer;
        private final ByteBuffer mReadBuffer;
        private final BlockingQueue<Message> mReceivedMessages;

        private UpdateHandler(
                MessageSerializer serializer,
                ByteBuffer readBuffer,
                BlockingQueue<Message> receivedMessages) {
            mSerializer = serializer;
            mReadBuffer = readBuffer;
            mReceivedMessages = receivedMessages;
        }

        @Override
        public void onNewClientData(ReadableChannel channel) throws IOException {
            BufferedReader reader = new BufferedReader(channel, mReadBuffer);
            reader.clear();

            try (DataInputStream dataInputStream = new DataInputStream(reader)) {
                Message message = mSerializer.read(dataInputStream);
                mReceivedMessages.add(message);
            }
        }

        @Override
        public void onNewChannel(int identifier) throws IOException {

        }
    }
}
