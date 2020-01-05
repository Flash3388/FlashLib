package com.flash3388.flashlib.communication.connection;

import com.flash3388.flashlib.communication.connection.socket.TcpClientConnector;
import com.flash3388.flashlib.communication.connection.socket.TcpServerConnector;
import com.flash3388.flashlib.io.Closer;
import com.flash3388.flashlib.util.concurrent.ExecutorCloser;
import org.junit.internal.Throwables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class TcpConnectionTest {

    private static final int DEFAULT_READ_TIMEOUT = 1000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000;

    private ExecutorService mExecutorService;
    private ServerSocket mServerSocket;
    private Closer mCloser;

    @BeforeEach
    public void setUp() throws Exception {
        mCloser = Closer.empty();

        mExecutorService = Executors.newSingleThreadExecutor();
        mCloser.add(new ExecutorCloser(mExecutorService));

        mServerSocket = new ServerSocket(0);
        mCloser.add(mServerSocket);
    }

    @AfterEach
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
        Function<Connection> clientTask = (clientConnection) -> clientConnection.write(DATA);

        connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    @Test
    public void read_clientNothingToRead_clientThrowsTimeoutException() throws Exception {
        final int READ_TIMEOUT = 200;

        Function<Connection> serverTask = (serverConnection) -> {
        };
        Function<Connection> clientTask = (clientConnection) -> clientConnection.read(1);

        assertThrows(TimeoutException.class, ()-> connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT));
    }

    @Test
    public void read_serverNothingToRead_serverThrowsTimeoutException() throws Exception {
        final int READ_TIMEOUT = 200;

        Function<Connection> serverTask = (serverConnection) -> serverConnection.read(1);
        Function<Connection> clientTask = (clientConnection) -> {
        };

        assertThrows(TimeoutException.class, ()-> connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT));
    }

    @Test
    public void read_clientDisconnectedInMiddle_serverThrowsEOFException() throws Exception {
        final int READ_TIMEOUT = 200;

        Function<Connection> serverTask = (serverConnection) -> serverConnection.read(1);
        Function<Connection> clientTask = Connection::close;

        assertThrows(EOFException.class, ()-> connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT));
    }

    @Test
    public void read_serverDisconnectedInMiddle_clientThrowsEOFException() throws Exception {
        final int READ_TIMEOUT = 200;

        Function<Connection> serverTask = Connection::close;
        Function<Connection> clientTask = (clientConnection) -> clientConnection.read(1);

        assertThrows(EOFException.class, ()-> connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT));
    }

    @Test
    public void connect_serverHasNoClient_throwsTimeoutException() throws Exception {
        final int CONNECTION_TIMEOUT = 200;

        try (TcpServerConnector serverConnector = new TcpServerConnector(mServerSocket, DEFAULT_READ_TIMEOUT)) {
            assertThrows(TimeoutException.class, () -> tryConnectExpectFailure(serverConnector, CONNECTION_TIMEOUT));
        }
    }

    @Test
    public void connect_clientHasNoServer_throwsConnectionFailedException() throws Exception {
        final int CONNECTION_TIMEOUT = 200;

        assertThrows(ConnectionFailedException.class, ()->{
            InetSocketAddress address = new InetSocketAddress(mServerSocket.getLocalPort() - 1);
            try (TcpClientConnector clientConnector = new TcpClientConnector(address, DEFAULT_READ_TIMEOUT)) {
                tryConnectExpectFailure(clientConnector, CONNECTION_TIMEOUT);
            }
        });
    }

    private void connectAndRun(Function<Connection> serverTask, Function<Connection> clientTask, int connectionTimeout, int readTimeout) throws Exception {
        TcpClientConnector clientConnector = new TcpClientConnector(new InetSocketAddress(mServerSocket.getLocalPort()), readTimeout);
        mCloser.add(clientConnector);

        TcpServerConnector serverConnector = new TcpServerConnector(mServerSocket, readTimeout);
        mCloser.add(serverConnector);

        CountDownLatch connectionLatch = new CountDownLatch(1);
        CyclicBarrier endTasksBarrier = new CyclicBarrier(2);

        Future<Void> clientFuture = mExecutorService.submit(() -> {
            connectionLatch.await();
            try (Connection connection = clientConnector.connect(connectionTimeout)) {
                clientTask.apply(connection);
            } finally {
                endTasksBarrier.await();
            }

            return null;
        });

        connectionLatch.countDown();
        try (Connection connection = serverConnector.connect(connectionTimeout)) {
            serverTask.apply(connection);
        } finally {
            endTasksBarrier.await();
        }

        // to throw an exception if necessary
        try {
            clientFuture.get();
        } catch (ExecutionException e) {
            throw Throwables.rethrowAsException(e.getCause());
        }
    }

    private void tryConnectExpectFailure(Connector connector, int timeout) throws Exception {
        try (Connection connection = connector.connect(timeout)) {
            fail("should not have connected");
        }
    }

    @FunctionalInterface
    private interface Function<T> {
        void apply(T value) throws Exception;
    }
}
