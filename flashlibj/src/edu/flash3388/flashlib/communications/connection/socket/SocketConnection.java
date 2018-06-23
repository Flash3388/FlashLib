package edu.flash3388.flashlib.communications.connection.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;

public class SocketConnection implements Connection {

	private Socket mSocket;
	
	private OutputStream mOut;
	private InputStream mIn;
	
	public SocketConnection(Socket socket) throws IOException {
		mSocket = socket;
		
		mOut = mSocket.getOutputStream();
		mIn = mSocket.getInputStream();
	}
	
	@Override
	public void write(byte[] data) throws IOException {
		mOut.write(data);
	}

	@Override
	public byte[] read(int count) throws IOException, TimeoutException {
		try {
			byte[] buffer = new byte[count];
			mIn.read(buffer, 0, count);
			
			return buffer;
		} catch (SocketTimeoutException e) {
			throw new TimeoutException(e);
		}
	}

}
