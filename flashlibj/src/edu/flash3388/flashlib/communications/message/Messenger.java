package edu.flash3388.flashlib.communications.message;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;
import edu.flash3388.flashlib.io.Closeables;
import edu.flash3388.flashlib.io.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Messenger {

    private static final int VERSION = 1;
    private static final int HEADER_LENGTH_SIZE = 4;

    private final Connection mConnection;
    private final Serializer mSerializer;

    public Messenger(Connection connection, Serializer serializer) {
        mConnection = connection;
        mSerializer = serializer;
    }

    public void writeMessage(Message message) throws WriteException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            mSerializer.serialize(outputStream, message);
            byte[] serializedMessage = outputStream.toByteArray();

            outputStream.reset();

            MessageHeader messageHeader = new MessageHeader(VERSION, serializedMessage.length);
            mSerializer.serialize(outputStream, messageHeader);
            byte[] serializedMessageHeader = outputStream.toByteArray();

            byte[] serializedHeaderLength = ByteBuffer.allocate(HEADER_LENGTH_SIZE).putInt(serializedMessageHeader.length).array();

            mConnection.write(serializedHeaderLength);
            mConnection.write(serializedMessageHeader);
            mConnection.write(serializedMessage);
        } catch (IOException e) {
            throw new WriteException(e);
        } finally {
            Closeables.closeQuietly(outputStream);
        }
    }

    public Message readMessage() throws ReadException {
        try {
            byte[] serializedHeaderLength = mConnection.read(HEADER_LENGTH_SIZE);
            int headerLength = ByteBuffer.wrap(serializedHeaderLength).getInt();

            byte[] serializedMessageHeader = mConnection.read(headerLength);
            MessageHeader messageHeader = mSerializer.deserialize(new ByteArrayInputStream(serializedMessageHeader), MessageHeader.class);

            ensureCompatibleVersion(messageHeader.getVersion());

            byte[] serializedMessage = mConnection.read(messageHeader.getMessageLength());
            return mSerializer.deserialize(new ByteArrayInputStream(serializedMessage), Message.class);
        } catch (IOException | TimeoutException | ClassNotFoundException e) {
            throw new ReadException(e);
        }
    }

    private void ensureCompatibleVersion(int otherVersion) {
        // TODO: IMPLEMENT
    }
}
