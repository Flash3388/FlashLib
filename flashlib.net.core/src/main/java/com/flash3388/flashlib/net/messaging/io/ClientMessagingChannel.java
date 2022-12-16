package com.flash3388.flashlib.net.messaging.io;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class ClientMessagingChannel implements MessagingChannel {

    private final TcpClientChannel mChannel;
    private final MessageSerializer mSerializer;

    public ClientMessagingChannel(SocketAddress serverAddress, KnownMessageTypes messageTypes) {
        mChannel = new TcpClientChannel(serverAddress);
        mSerializer = new MessageSerializer(messageTypes);
    }

    @Override
    public boolean establishConnection() throws IOException, TimeoutException {
        return mChannel.refreshConnection();
    }

    @Override
    public void write(Message message) throws IOException, TimeoutException {
        try (ChannelOutput output = mChannel.output();
             DataOutputStream dataOutputStream = new DataOutputStream(output)) {
            mSerializer.write(dataOutputStream, message);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException();
        }
    }

    @Override
    public Message read() throws IOException, TimeoutException, InterruptedException {
        DataInput dataInput = mChannel.input();
        try {
            return mSerializer.read(dataInput);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException();
        }
    }
}
