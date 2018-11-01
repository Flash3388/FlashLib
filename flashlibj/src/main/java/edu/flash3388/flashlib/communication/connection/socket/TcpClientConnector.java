package edu.flash3388.flashlib.communication.connection.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import edu.flash3388.flashlib.communication.connection.Connection;
import edu.flash3388.flashlib.communication.connection.ConnectionFailedException;
import edu.flash3388.flashlib.communication.connection.Connector;
import edu.flash3388.flashlib.communication.connection.TimeoutException;
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
	public Connection connect(int connectionTimeout) throws ConnectionFailedException, TimeoutException {
		Socket socket = new Socket();

		try {
			return Closer.with(socket).run(()->{
				socket.connect(mEndPoint, connectionTimeout);
				socket.setSoTimeout(mReadTimeout);
				
				return new SocketConnection(socket);
			}, CloseOption.CLOSE_ON_ERROR);
		} catch (SocketTimeoutException e) {
		    throw new TimeoutException(e);
        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        }
	}

	@Override
	public void close() throws IOException {
		
	}
}
