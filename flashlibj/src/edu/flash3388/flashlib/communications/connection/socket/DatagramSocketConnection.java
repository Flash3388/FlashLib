package edu.flash3388.flashlib.communications.connection.socket;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class DatagramSocketConnection implements Connection {

    private DatagramSocket mSocket;

    public DatagramSocketConnection(DatagramSocket socket) {
        mSocket = socket;
    }

    @Override
    public void write(int data) throws IOException {
        write(new byte[] {(byte) data});
    }

    @Override
    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    @Override
    public void write(byte[] data, int start, int length) throws IOException {
        mSocket.send(new DatagramPacket(data, start, length));
    }

    @Override
    public int read() throws IOException, TimeoutException {
        return read(1)[0];
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
    public byte[] read(int count) throws IOException, TimeoutException {
        byte[] data = new byte[count];
        read(data, 0, data.length);
        return data;
    }

    @Override
    public void close() throws IOException {
        mSocket.disconnect();
        mSocket.close();
    }
}
