package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.io.MessageSerializer;
import com.flash3388.flashlib.net.messaging.io.MessagingServerChannel;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class ServerMessagingChannel implements MessagingServerChannel {

    private final TcpServerChannel mChannel;
    private final MessageSerializer mSerializer;

    public ServerMessagingChannel(SocketAddress bindAddress, KnownMessageTypes messageTypes) {
        mChannel = new TcpServerChannel(bindAddress);
        mSerializer = new MessageSerializer(messageTypes);
    }

    @Override
    public void handleNewConnections() throws IOException, TimeoutException, InterruptedException {
        mChannel.acceptNewClient();
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
        return mChannel.waitForReadAvailable((reader)-> {
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
