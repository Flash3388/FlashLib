package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.impl.TcpClientChannel;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.io.MessageSerializer;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class ClientMessagingChannel implements MessagingChannel {

    private final TcpClientChannel mChannel;
    private final MessageSerializer mSerializer;

    public ClientMessagingChannel(SocketAddress serverAddress, KnownMessageTypes messageTypes, Logger logger) {
        mChannel = new TcpClientChannel(serverAddress, logger);
        mSerializer = new MessageSerializer(messageTypes);
    }

    @Override
    public void waitForConnection() throws IOException, TimeoutException, InterruptedException {
        mChannel.waitForConnection();
    }

    @Override
    public void write(Message message) throws IOException, TimeoutException, InterruptedException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            mSerializer.write(dataOutputStream, message);
            dataOutputStream.flush();

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
            mChannel.write(buffer);
        }
    }

    @Override
    public Message read() throws IOException, TimeoutException, InterruptedException {
        return mChannel.read((reader)-> {
            try (DataInputStream dataInputStream = new DataInputStream(reader)) {
                return mSerializer.read(dataInputStream);
            }
        });
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
