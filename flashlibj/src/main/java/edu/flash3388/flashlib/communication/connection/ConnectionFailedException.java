package edu.flash3388.flashlib.communication.connection;

public class ConnectionFailedException extends Exception {

	public ConnectionFailedException(String message) {
		super(message);
	}
	
	public ConnectionFailedException(Throwable cause) {
		super(cause);
	}
}
