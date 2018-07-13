package edu.flash3388.flashlib.communications.connection.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.ConnectionFailedException;
import edu.flash3388.flashlib.communications.connection.Connector;
import edu.flash3388.flashlib.io.CloseOption;
import edu.flash3388.flashlib.io.Closer;

public class TcpServerConnector implements Connector {

	private ServerSocket mServerSocket;
	private int mReadTimeout;
	
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
