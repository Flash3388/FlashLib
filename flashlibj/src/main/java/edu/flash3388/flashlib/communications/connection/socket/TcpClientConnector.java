package edu.flash3388.flashlib.communications.connection.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.ConnectionFailedException;
import edu.flash3388.flashlib.communications.connection.Connector;
import edu.flash3388.flashlib.io.CloseOption;
import edu.flash3388.flashlib.io.Closer;

public class TcpClientConnector implements Connector {

	private final SocketAddress mEndPoint;
	private final int mReadTimeout;
	
	public TcpClientConnector(SocketAddress endPoint, int readTimeout) {
		mEndPoint = endPoint;
		mReadTimeout = readTimeout;
	}
	
	@Override
	public Connection connect(int connectionTimeout) throws ConnectionFailedException {
		Socket socket = new Socket();
		try {
			return Closer.with(socket).run(()->{
				socket.connect(mEndPoint, connectionTimeout);
				socket.setSoTimeout(mReadTimeout);
				
				return new SocketConnection(socket);
			}, CloseOption.CLOSE_ON_ERROR);
		} catch (IOException e) {
			throw new ConnectionFailedException(e);
		}
	}

	@Override
	public void close() throws IOException {
		
	}
}
