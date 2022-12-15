package com.flash3388.flashlib.hmi.comm.impl;

import com.castle.net.Connector;
import com.castle.net.StreamConnection;
import com.castle.time.Time;
import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.hmi.comm.BasicMessage;
import com.flash3388.flashlib.hmi.comm.io.MessageChannel;
import com.flash3388.flashlib.hmi.comm.v1.MessageHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageChannelImpl implements MessageChannel {

    private final Connector<StreamConnection> mConnector;
    private StreamConnection mConnection;

    public MessageChannelImpl(Connector<StreamConnection> connector) {
        mConnector = connector;
    }

    @Override
    public void writeMessage(BasicMessage message) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            byte[] content = message.getContent();
            MessageHeader header = new MessageHeader(message.getType(), content.length);
            header.writeTo(dataOutputStream);
            dataOutputStream.write(content);

            StreamConnection connection = getConnection();
            connection.outputStream().write(outputStream.toByteArray());
        } catch (TimeoutException e) {
            throw new IOException(e);
        }
    }

    @Override
    public BasicMessage readMessage(com.flash3388.flashlib.time.Time readTimeout) throws IOException {
        byte[] headerBytes = new byte[4 * 3]; // 3 * int
        mConnection.inputStream().read(headerBytes);

        MessageHeader header;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(headerBytes);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            header = new MessageHeader(dataInputStream);
        }

        byte[] content = new byte[header.getContentSize()];
        mConnection.inputStream().read(content);

        return new BasicMessage.Impl(header.getType(), content);
    }

    private synchronized StreamConnection getConnection() throws IOException, TimeoutException {
        if (mConnection == null) {
            mConnection = mConnector.connect(Time.milliseconds(500));
        }

        return mConnection;
    }
}
