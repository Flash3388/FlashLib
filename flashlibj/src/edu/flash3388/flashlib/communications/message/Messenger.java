package edu.flash3388.flashlib.communications.message;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.io.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Messenger {

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
            mConnection.write(serializedMessage);
        } catch (IOException e) {
            throw new WriteException(e);
        }
    }

    public Message readMessage() throws ReadException {
        throw new UnsupportedOperationException();
    }
}
