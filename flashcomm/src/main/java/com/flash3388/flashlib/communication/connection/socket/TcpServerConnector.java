package com.flash3388.flashlib.communication.connection.socket;

import com.flash3388.flashlib.communication.connection.Connection;
import com.flash3388.flashlib.communication.connection.ConnectionFailedException;
import com.flash3388.flashlib.communication.connection.Connector;
import com.flash3388.flashlib.communication.connection.TimeoutException;
import com.flash3388.flashlib.io.CloseOption;
import com.flash3388.flashlib.io.Closer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TcpServerConnector implements Connector {

	private final ServerSocket mServerSocket;
	private final int mReadTimeout;
	
	public TcpServerConnector(ServerSocket serverSocket, int readTimeout) {
		mServerSocket = serverSocket;
		mReadTimeout = readTimeout;
	}

	@Override
	public Connection connect(int connectionTimeout) throws ConnectionFailedException, TimeoutException {
		try {
		    mServerSocket.setSoTimeout(connectionTimeout);

			Socket socket = mServerSocket.accept();
			return Closer.with(socket).run(()-> {
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
		mServerSocket.close();
	}
}
