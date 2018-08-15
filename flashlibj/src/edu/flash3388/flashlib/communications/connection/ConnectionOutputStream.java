package edu.flash3388.flashlib.communications.connection;

import java.io.IOException;
import java.io.OutputStream;

public class ConnectionOutputStream extends OutputStream {

    private final Connection mConnection;

    public ConnectionOutputStream(Connection connection) {
        mConnection = connection;
    }

    @Override
    public void write(int b) throws IOException {
        mConnection.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        mConnection.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        mConnection.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        mConnection.close();
    }
}
