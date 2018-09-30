package edu.flash3388.flashlib.communications.connection.socket;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class DatagramSocketConnection implements Connection {

    private final DatagramSocket mSocket;

    public DatagramSocketConnection(DatagramSocket socket) {
        mSocket = socket;
    }

    @Override
    public void write(byte[] data, int start, int length) throws IOException {
        mSocket.send(new DatagramPacket(data, start, length));
    }

    @Override
    public int read(byte[] bytes, int start, int length) throws IOException, TimeoutException {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(bytes, start, length);
            mSocket.receive(datagramPacket);
            return length - start;
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public void close() throws IOException {
        mSocket.disconnect();
        mSocket.close();
    }
}
