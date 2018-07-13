package edu.flash3388.flashlib.communications.connection.socket;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.ConnectionFailedException;
import edu.flash3388.flashlib.communications.connection.Connector;
import edu.flash3388.flashlib.io.Closer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class DatagramConnector implements Connector {

    private SocketAddress mLocalAddress;
    private SocketAddress mRemoteAddress;
    private int mReadTimeout;

    public DatagramConnector(SocketAddress localAddress, SocketAddress remoteAddress, int readTimeoutMs) {
        mLocalAddress = localAddress;
        mRemoteAddress = remoteAddress;
        mReadTimeout = readTimeoutMs;
    }

    @Override
    public Connection connect(int connectionTimeout) throws ConnectionFailedException {

        try {
            DatagramSocket socket = new DatagramSocket();

            return Closer.onError(socket).run(()-> {
                socket.bind(mLocalAddress);
                socket.connect(mRemoteAddress);
                socket.setSoTimeout(mReadTimeout);

                return new DatagramSocketConnection(socket);
            });
        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
