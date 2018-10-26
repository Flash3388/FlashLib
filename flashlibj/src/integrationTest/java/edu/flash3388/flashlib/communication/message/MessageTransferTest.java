package edu.flash3388.flashlib.communication.message;

import edu.flash3388.flashlib.communication.connection.Connection;
import edu.flash3388.flashlib.communication.connection.FakeConnection;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.io.Serializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class MessageTransferTest {

    private ByteBuffer mSharedBuffer;
    private Connection mSendingConnection;
    private Connection mReadingConnection;
    private Serializer mSerializer;

    private Closer mCloser;

    @Before
    public void setUp() throws Exception {
        mCloser = Closer.empty();

        mSerializer = new Serializer();

        mSharedBuffer = ByteBuffer.allocate(1024);

        mSendingConnection = new FakeConnection(mSharedBuffer);
        mCloser.add(mSendingConnection);

        mReadingConnection = new FakeConnection(mSharedBuffer);
        mCloser.add(mReadingConnection);
    }

    @After
    public void tearDown() throws Exception {
        mCloser.close();
    }

    @Test
    public void transferMessage_overFakeConnection_messageRemainsTheSame() throws Exception {
        Messenger sendingMessenger = new Messenger(mSendingConnection, mSerializer);
        Messenger readingMessenger = new Messenger(mReadingConnection, mSerializer);

        StubMessage sentMessage = new StubMessage();
        sentMessage.mData = 5;

        sendingMessenger.writeMessage(sentMessage);

        mSharedBuffer.flip();

        Message readMessage = readingMessenger.readMessage();

        assertEquals(readMessage, sentMessage);
    }

    private static class StubMessage implements Message {
        int mData;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StubMessage)) {
                return false;
            }

            StubMessage other = (StubMessage) obj;
            return other.mData == mData;
        }
    }
}
