package com.flash3388.flashlib.communication.message;

import com.flash3388.flashlib.communication.connection.Connection;
import com.flash3388.flashlib.communication.connection.TimeoutException;
import com.flash3388.flashlib.io.serialization.Serializer;
import com.flash3388.flashlib.io.serialization.TypeException;
import com.flash3388.flashlib.util.versioning.IncompatibleVersionException;
import com.flash3388.flashlib.util.versioning.Version;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Messenger {

    public static final Version VERSION = new Version(1, 0, 0);
    private static final int HEADER_LENGTH_SIZE = 4;

    private final Connection mConnection;
    private final Serializer mSerializer;

    public Messenger(Connection connection, Serializer serializer) {
        mConnection = connection;
        mSerializer = serializer;
    }

    public void writeMessage(Message message) throws WriteException {
        try {
            byte[] serializedMessage = mSerializer.serialize(message);

            MessageHeader messageHeader = new MessageHeader(VERSION, serializedMessage.length);
            byte[] serializedMessageHeader = mSerializer.serialize(messageHeader);

            byte[] serializedHeaderLength = ByteBuffer.allocate(HEADER_LENGTH_SIZE).putInt(serializedMessageHeader.length).array();

            mConnection.write(serializedHeaderLength);
            mConnection.write(serializedMessageHeader);
            mConnection.write(serializedMessage);
        } catch (IOException e) {
            throw new WriteException(e);
        }
    }

    public Message readMessage() throws ReadException {
        try {
            byte[] serializedHeaderLength = mConnection.read(HEADER_LENGTH_SIZE);
            int headerLength = ByteBuffer.wrap(serializedHeaderLength).getInt();

            byte[] serializedMessageHeader = mConnection.read(headerLength);
            MessageHeader messageHeader = mSerializer.deserialize(serializedMessageHeader, MessageHeader.class);

            ensureCompatibleVersion(messageHeader.getVersion());

            byte[] serializedMessage = mConnection.read(messageHeader.getMessageLength());
            return mSerializer.deserialize(serializedMessage, Message.class);
        } catch (IOException | TimeoutException | TypeException e) {
            throw new ReadException(e);
        }
    }

    private void ensureCompatibleVersion(Version other) {
        if (!VERSION.isCompatibleWith(other)) {
            throw new IncompatibleVersionException(VERSION, other);
        }
    }
}
