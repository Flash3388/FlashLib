package edu.flash3388.flashlib.communications.connection;

public class TimeoutException extends Exception {

	public TimeoutException(String message) {
		super(message);
	}
	
	public TimeoutException(Throwable cause) {
		super(cause);
	}
}
