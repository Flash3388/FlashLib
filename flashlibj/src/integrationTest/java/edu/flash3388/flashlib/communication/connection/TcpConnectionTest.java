package edu.flash3388.flashlib.communication.connection;

import edu.flash3388.flashlib.communication.connection.socket.TcpClientConnector;
import edu.flash3388.flashlib.communication.connection.socket.TcpServerConnector;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.util.concurrent.ExecutorTerminator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.*;

import static org.junit.Assert.assertArrayEquals;

public class TcpConnectionTest {

    private static final int PORT = 10000;
    private static final int READ_TIMEOUT = 1000;
    private static final int CONNECTION_TIMEOUT = 1000;

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

        TcpClientConnector clientConnector = new TcpClientConnector(new InetSocketAddress(PORT), READ_TIMEOUT);
        TcpServerConnector serverConnector = new TcpServerConnector(mServerSocket, READ_TIMEOUT);

        CountDownLatch connectionLatch = new CountDownLatch(1);
        CountDownLatch writeLatch = new CountDownLatch(1);

        mExecutorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                connectionLatch.await();
                Connection connection = clientConnector.connect(CONNECTION_TIMEOUT);
                try {
                    writeLatch.countDown();
                    connection.write(DATA);
                } finally {
                    connection.close();
                }

                return null;
            }
        });

        byte[] readData;

        connectionLatch.countDown();
        Connection connection = serverConnector.connect(CONNECTION_TIMEOUT);
        try {
            writeLatch.await();
            readData = connection.read(DATA.length);
        } finally {
            connection.close();
        }

        assertArrayEquals(DATA, readData);
    }
}
