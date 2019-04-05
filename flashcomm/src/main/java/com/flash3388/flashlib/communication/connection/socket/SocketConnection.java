package com.flash3388.flashlib.communication.connection.socket;

import com.flash3388.flashlib.communication.connection.Connection;
import com.flash3388.flashlib.communication.connection.TimeoutException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketConnection implements Connection {

	private final Socket mSocket;
	
	private final OutputStream mOut;
	private final DataInputStream mIn;
	
	public SocketConnection(Socket socket) throws IOException {
		mSocket = socket;
		
		mOut = mSocket.getOutputStream();
		mIn = new DataInputStream(mSocket.getInputStream());
	}

    @Override
    public void write(byte[] data, int start, int length) throws IOException {
        mOut.write(data, start, length);
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
	public void close() throws IOException {
	    // closes the output and input streams
		mSocket.close();
	}
}
