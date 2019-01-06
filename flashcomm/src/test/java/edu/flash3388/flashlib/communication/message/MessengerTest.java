package edu.flash3388.flashlib.communication.message;

import edu.flash3388.flashlib.communication.connection.Connection;
import edu.flash3388.flashlib.io.serialization.Serializer;
import edu.flash3388.flashlib.util.versioning.IncompatibleVersionException;
import edu.flash3388.flashlib.util.versioning.Version;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.nio.ByteBuffer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MessengerTest {

    @Test
    public void writeMessage_standard_writesInfoInOrder() throws Exception {
        byte[] SERIALIZED_DATA = {0x1};
        byte[] SERIALIZED_HEADER = {0x3, 0x4};
        byte[] SERIALIZED_HEADER_LENGTH = ByteBuffer.allocate(4).putInt(SERIALIZED_HEADER.length).array();

        Connection connection = mock(Connection.class);
        Serializer serializer = mock(Serializer.class);

        mockSerializeOnType(serializer, SERIALIZED_DATA, Message.class);
        mockSerializeOnType(serializer, SERIALIZED_HEADER, MessageHeader.class);

        Message message = mock(Message.class);

        Messenger messenger = new Messenger(connection, serializer);
        messenger.writeMessage(message);

        InOrder inOrder = Mockito.inOrder(connection);
        inOrder.verify(connection).write(SERIALIZED_HEADER_LENGTH);
        inOrder.verify(connection).write(SERIALIZED_HEADER);
        inOrder.verify(connection).write(SERIALIZED_DATA);
    }

    @Test
    public void readMessage_standard_readsInfoInOrder() throws Exception {
        byte[] SERIALIZED_DATA = {0x1};
        byte[] SERIALIZED_HEADER = {0x3, 0x4};
        byte[] SERIALIZED_HEADER_LENGTH = ByteBuffer.allocate(4).putInt(SERIALIZED_HEADER.length).array();

        MessageHeader HEADER = new MessageHeader(Messenger.VERSION, SERIALIZED_DATA.length);
        Message MESSAGE = mock(Message.class);

        Connection connection = mock(Connection.class);
        Serializer serializer = mock(Serializer.class);

        mockReadOnRequestedLength(connection, SERIALIZED_HEADER_LENGTH, 4);
        mockReadOnRequestedLength(connection, SERIALIZED_HEADER, SERIALIZED_HEADER.length);
        mockReadOnRequestedLength(connection, SERIALIZED_DATA, SERIALIZED_DATA.length);

        mockDeserializeOnType(serializer, HEADER, MessageHeader.class);
        mockDeserializeOnType(serializer, MESSAGE, Message.class);

        Messenger messenger = new Messenger(connection, serializer);
        messenger.readMessage();

        InOrder inOrder = Mockito.inOrder(connection);
        inOrder.verify(connection).read(4);
        inOrder.verify(connection).read(SERIALIZED_HEADER.length);
        inOrder.verify(connection).read(SERIALIZED_DATA.length);
    }

    @Test(expected = IncompatibleVersionException.class)
    public void readMessage_majorVersionIsHigher_throwsIncompatibleVersionException() throws Exception {
        Version VERSION = new Version(2, 0, 0);
        Messenger messenger = mockForReturningVersion(VERSION);
        messenger.readMessage();
    }

    @Test(expected = IncompatibleVersionException.class)
    public void readMessage_minorVersionIsHigher_throwsIncompatibleVersionException() throws Exception {
        Version VERSION = new Version(1, 2, 0);
        Messenger messenger = mockForReturningVersion(VERSION);
        messenger.readMessage();
    }

    @Test
    public void readMessage_minorVersionIsLower_compatibleVersion() throws Exception {
        Version VERSION = new Version(1, -1, 0);
        Messenger messenger = mockForReturningVersion(VERSION);
        messenger.readMessage();
    }

    private void mockSerializeOnType(Serializer serializer, byte[] data, Class<?> type) throws Exception {
        doAnswer(new Answer<byte[]>() {
            @Override
            public byte[] answer(InvocationOnMock invocation) throws Throwable {
                return data;
            }
        }).when(serializer).serialize(any(type));
    }

    private void mockReadOnRequestedLength(Connection connection, byte[] data, int requestedLength) throws Exception {
        when(connection.read(requestedLength)).thenReturn(data);
    }

    private <T> void mockDeserializeOnType(Serializer serializer, T data, Class<T> type) throws Exception {
        when(serializer.deserialize(any(byte[].class), eq(type))).thenReturn(data);
    }

    private Messenger mockForReturningVersion(Version version) throws Exception {
        byte[] SERIALIZED_DATA = {0x1};
        byte[] SERIALIZED_HEADER = {0x3, 0x4};
        byte[] SERIALIZED_HEADER_LENGTH = ByteBuffer.allocate(4).putInt(SERIALIZED_HEADER.length).array();

        MessageHeader HEADER = new MessageHeader(version, SERIALIZED_DATA.length);
        Message MESSAGE = mock(Message.class);

        Connection connection = mock(Connection.class);
        Serializer serializer = mock(Serializer.class);

        mockReadOnRequestedLength(connection, SERIALIZED_HEADER_LENGTH, 4);
        mockReadOnRequestedLength(connection, SERIALIZED_HEADER, SERIALIZED_HEADER.length);
        mockReadOnRequestedLength(connection, SERIALIZED_DATA, SERIALIZED_DATA.length);

        mockDeserializeOnType(serializer, HEADER, MessageHeader.class);
        mockDeserializeOnType(serializer, MESSAGE, Message.class);

        return new Messenger(connection, serializer);
    }
}