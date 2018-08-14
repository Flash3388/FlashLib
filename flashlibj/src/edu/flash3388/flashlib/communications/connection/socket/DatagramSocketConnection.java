package edu.flash3388.flashlib.communications.connection.socket;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DatagramSocketConnection implements Connection {

    private DatagramSocket mSocket;

    public DatagramSocketConnection(DatagramSocket socket) {
        mSocket = socket;
    }

    @Override
    public void write(byte[] data) throws IOException {
        mSocket.send(new DatagramPacket(data, data.length));
    }

    @Override
    public byte[] read(int count) throws IOException, TimeoutException {
        byte[] data = new byte[count];

        DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
        mSocket.receive(datagramPacket);

        return data;
    }

    @Override
    public void close() throws IOException {
        mSocket.disconnect();
        mSocket.close();
    }
}
