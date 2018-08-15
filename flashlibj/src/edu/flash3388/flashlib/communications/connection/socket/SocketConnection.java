package edu.flash3388.flashlib.communications.connection.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;

public class SocketConnection implements Connection {

	private Socket mSocket;
	
	private OutputStream mOut;
	private DataInputStream mIn;
	
	public SocketConnection(Socket socket) throws IOException {
		mSocket = socket;
		
		mOut = mSocket.getOutputStream();
		mIn = new DataInputStream(mSocket.getInputStream());
	}

    @Override
    public void write(int data) throws IOException {
        mOut.write(data);
    }

    @Override
	public void write(byte[] data) throws IOException {
		mOut.write(data);
	}

    @Override
    public void write(byte[] data, int start, int length) throws IOException {
        mOut.write(data, start, length);
    }

    @Override
    public int read() throws IOException, TimeoutException {
        try {
            return mIn.read();
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public int read(byte[] bytes, int start, int length) throws IOException, TimeoutException {
        try {
            mIn.readFully(bytes, start, length);
            return length;
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
	public byte[] read(int count) throws IOException, TimeoutException {
		byte[] buffer = new byte[count];
		read(buffer, 0, count);
		return buffer;
	}

	@Override
	public void close() throws IOException {
		mSocket.close();
	}
}
