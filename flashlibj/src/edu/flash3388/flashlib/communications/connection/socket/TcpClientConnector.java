package edu.flash3388.flashlib.communications.connection.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.ConnectionFailedException;
import edu.flash3388.flashlib.communications.connection.Connector;
import edu.flash3388.flashlib.io.Closer;

public class TcpClientConnector implements Connector {

	private SocketAddress mEndPoint;
	private int mReadTimeout;
	
	public TcpClientConnector(SocketAddress endPoint, int readTimeout) {
		mEndPoint = endPoint;
		mReadTimeout = readTimeout;
	}
	
	@Override
	public Connection connect(int connectionTimeout) throws ConnectionFailedException {
		Socket socket = new Socket();
		try {
			return Closer.onError(socket).run(()->{
				socket.connect(mEndPoint, connectionTimeout);
				socket.setSoTimeout(mReadTimeout);
				
				return new SocketConnection(socket);
			});
		} catch (IOException e) {
			throw new ConnectionFailedException(e);
		}
	}

	@Override
	public void close() throws IOException {
		
	}
}
