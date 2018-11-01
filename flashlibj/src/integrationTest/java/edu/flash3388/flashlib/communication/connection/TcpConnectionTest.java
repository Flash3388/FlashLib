package edu.flash3388.flashlib.communication.connection;

import edu.flash3388.flashlib.communication.connection.socket.TcpClientConnector;
import edu.flash3388.flashlib.communication.connection.socket.TcpServerConnector;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.util.concurrent.ExecutorTerminator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class TcpConnectionTest {

    private static final int PORT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 1000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000;

    private ExecutorService mExecutorService;
    private ServerSocket mServerSocket;
    private Closer mCloser;

    @Before
    public void setUp() throws Exception {
        mCloser = Closer.empty();

        mExecutorService = Executors.newSingleThreadExecutor();
        mCloser.add(new ExecutorTerminator(mExecutorService));

        mServerSocket = new ServerSocket(PORT);
        mCloser.add(mServerSocket);
    }

    @After
    public void tearDown() throws Exception {
        mCloser.close();
    }

    @Test
    public void connecting_clientAndServer_success() throws Exception {
        byte[] DATA = {0x1, 0x3};

        Function<Connection> serverTask = (serverConnection) -> {
            byte[] data = serverConnection.read(DATA.length);
            assertArrayEquals(DATA, data);
        };
        Function<Connection> clientTask = (clientConnection) -> {
            clientConnection.write(DATA);
        };

        connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    @Test(expected = TimeoutException.class)
    public void read_nothingToRead_throwsTimeoutException() throws Exception {
        final int READ_TIMEOUT = 200;

        Function<Connection> serverTask = (serverConnection) -> {
        };
        Function<Connection> clientTask = (clientConnection) -> {
            clientConnection.read(1);
        };

        connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT);
    }

    @Test(expected = TimeoutException.class)
    public void connect_serverHasNoClient_throwsTimeoutException() throws Exception {
        final int CONNECTION_TIMEOUT = 200;

        TcpServerConnector serverConnector = new TcpServerConnector(mServerSocket, DEFAULT_READ_TIMEOUT);
        tryConnectExpectFailure(serverConnector, CONNECTION_TIMEOUT);
    }

    @Test(expected = TimeoutException.class)
    public void connect_clientHasNoClient_throwsTimeoutException() throws Exception {
        final int CONNECTION_TIMEOUT = 200;

        InetSocketAddress address = new InetSocketAddress(InetAddress.getLoopbackAddress(), PORT);
        TcpClientConnector clientConnector = new TcpClientConnector(address, DEFAULT_READ_TIMEOUT);
        tryConnectExpectFailure(clientConnector, CONNECTION_TIMEOUT);
    }

    private void connectAndRun(Function<Connection> serverTask, Function<Connection> clientTask, int connectionTimeout, int readTImeout) throws Exception {
        TcpClientConnector clientConnector = new TcpClientConnector(new InetSocketAddress(PORT), readTImeout);
        TcpServerConnector serverConnector = new TcpServerConnector(mServerSocket, readTImeout);

        CountDownLatch connectionLatch = new CountDownLatch(1);
        CountDownLatch tasksLatch = new CountDownLatch(1);

        Future<Void> clientFuture = mExecutorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                connectionLatch.await();
                Connection connection = clientConnector.connect(connectionTimeout);
                try {
                    tasksLatch.countDown();
                    clientTask.apply(connection);
                } finally {
                    connection.close();
                }

                return null;
            }
        });

        connectionLatch.countDown();
        Connection connection = serverConnector.connect(connectionTimeout);
        try {
            tasksLatch.await();
            serverTask.apply(connection);
        } finally {
            connection.close();
        }

        // to throw an exception if necessary
        try {
            clientFuture.get();
        } catch (ExecutionException e) {
            Throwable throwable = e.getCause();
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if(throwable instanceof Exception) {
                throw (Exception) throwable;
            }
        }
    }

    private void tryConnectExpectFailure(Connector connector, int timeout) throws Exception {
        try {
            Connection connection = connector.connect(timeout);
            try {
                fail("should not have connected");
            } finally {
                connection.close();
            }
        } finally {
            connector.close();
        }
    }

    @FunctionalInterface
    private interface Function<T> {
        void apply(T value) throws Exception;
    }
}
