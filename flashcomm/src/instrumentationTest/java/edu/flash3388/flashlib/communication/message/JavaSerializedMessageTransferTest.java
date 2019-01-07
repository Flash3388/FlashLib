package edu.flash3388.flashlib.communication.message;

import edu.flash3388.flashlib.communication.connection.Connection;
import edu.flash3388.flashlib.communication.connection.FakeConnection;
import edu.flash3388.flashlib.communication.connection.socket.TcpClientConnector;
import edu.flash3388.flashlib.communication.connection.socket.TcpServerConnector;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.io.serialization.JavaObjectSerializer;
import edu.flash3388.flashlib.io.serialization.Serializer;
import edu.flash3388.flashlib.util.concurrent.ExecutorCloser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class JavaSerializedMessageTransferTest {

    public static class TransferOverFakeConnectionTest {

        private ByteBuffer mSharedBuffer;
        private Connection mSendingConnection;
        private Connection mReadingConnection;
        private Serializer mSerializer;

        private Closer mCloser;

        @Before
        public void setUp() throws Exception {
            mCloser = Closer.empty();

            mSerializer = new JavaObjectSerializer();

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
    }

    public static class TransferOverTcpConnectionTest {

        private static final int READ_TIMEOUT = 1000;
        private static final int CONNECTION_TIMEOUT = 1000;

        private Connection mSendingConnection;
        private Connection mReadingConnection;
        private Serializer mSerializer;

        private Closer mCloser;

        @Before
        public void setUp() throws Exception {
            mCloser = Closer.empty();

            mSerializer = new JavaObjectSerializer();
            connectClientAndSocket();
        }

        @After
        public void tearDown() throws Exception {
            mCloser.close();
        }

        @Test
        public void transferMessage_overTcpConnection_messageRemainsTheSame() throws Exception {
            Messenger sendingMessenger = new Messenger(mSendingConnection, mSerializer);
            Messenger readingMessenger = new Messenger(mReadingConnection, mSerializer);

            StubMessage sentMessage = new StubMessage();
            sentMessage.mData = 5;

            sendingMessenger.writeMessage(sentMessage);

            Message readMessage = readingMessenger.readMessage();

            assertEquals(readMessage, sentMessage);
        }

        private void connectClientAndSocket() throws Exception {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            mCloser.add(new ExecutorCloser(executorService));

            ServerSocket serverSocket = new ServerSocket(0);
            mCloser.add(serverSocket);

            TcpServerConnector serverConnector = new TcpServerConnector(serverSocket, READ_TIMEOUT);
            mCloser.add(serverConnector);

            TcpClientConnector clientConnector = new TcpClientConnector(new InetSocketAddress(serverSocket.getLocalPort()), READ_TIMEOUT);
            mCloser.add(clientConnector);

            CountDownLatch connectionLatch = new CountDownLatch(1);

            Future<Connection> clientConnectionFuture = executorService.submit(new Callable<Connection>() {
                @Override
                public Connection call() throws Exception {
                    connectionLatch.await();
                    return clientConnector.connect(CONNECTION_TIMEOUT);
                }
            });

            connectionLatch.countDown();
            mSendingConnection = serverConnector.connect(CONNECTION_TIMEOUT);
            mCloser.add(mSendingConnection);

            mReadingConnection = clientConnectionFuture.get();
            mCloser.add(mReadingConnection);
        }
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
