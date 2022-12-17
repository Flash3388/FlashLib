package com.flash3388.flashlib.net.old.messanger;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.AutoConnectingChannel;
import com.flash3388.flashlib.net.BufferedChannelReader;
import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.message.MessageSerializer;
import com.flash3388.flashlib.net.tcp.TcpClientConnector;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class ClientMessagingChannel implements MessagingChannel {

    private final AutoConnectingChannel mChannel;
    private final MessageSerializer mSerializer;
    private final ByteBuffer mReadBuffer;

    public ClientMessagingChannel(SocketAddress serverAddress,
                                  KnownMessageTypes messageTypes,
                                  Logger logger) {
        mChannel = new AutoConnectingChannel(new TcpClientConnector(logger), serverAddress, logger);
        mSerializer = new MessageSerializer(ourId, messageTypes);
        mReadBuffer = ByteBuffer.allocateDirect(1024);
    }

    @Override
    public void setOnConnection(Runnable callback) {
        mChannel.setOnConnection(callback);
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
        BufferedChannelReader reader = new BufferedChannelReader(mChannel, mReadBuffer);
        reader.clear();

        try (DataInputStream dataInputStream = new DataInputStream(reader)) {
            return mSerializer.read(dataInputStream);
        }
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
