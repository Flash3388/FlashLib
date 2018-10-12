package edu.flash3388.flashlib.communication.connection.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.flash3388.flashlib.communication.connection.Connection;
import edu.flash3388.flashlib.communication.connection.ConnectionFailedException;
import edu.flash3388.flashlib.communication.connection.Connector;
import edu.flash3388.flashlib.io.CloseOption;
import edu.flash3388.flashlib.io.Closer;

public class TcpServerConnector implements Connector {

	private final ServerSocket mServerSocket;
	private final int mReadTimeout;
	
	public TcpServerConnector(ServerSocket serverSocket, int readTimeout) {
		mServerSocket = serverSocket;
		mReadTimeout = readTimeout;
	}

	@Override
	public Connection connect(int connectionTimeout) throws ConnectionFailedException {
		try {
			Socket socket = mServerSocket.accept();
			return Closer.with(socket).run(()-> {
				socket.setSoTimeout(mReadTimeout);
				
				return new SocketConnection(socket);
			}, CloseOption.CLOSE_ON_ERROR);
		} catch (IOException e) {
			throw new ConnectionFailedException(e);
		}
	}

	@Override
	public void close() throws IOException {
		mServerSocket.close();
	}
}
