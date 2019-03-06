package com.flash3388.flashlib.communication.connection;

import java.io.IOException;
import java.io.InputStream;

public class ConnectionInputStream extends InputStream {

    private final Connection mConnection;

    public ConnectionInputStream(Connection connection) {
        mConnection = connection;
    }

    @Override
    public int read() throws IOException {
        try {
            byte[] result = mConnection.read(1);
            return result[0];
        } catch (TimeoutException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            return mConnection.read(b, off, len);
        } catch (TimeoutException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        mConnection.close();
    }
}
