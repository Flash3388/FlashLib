package com.flash3388.flashlib.communication.connection;

public class TimeoutException extends Exception {

	public TimeoutException(String message) {
		super(message);
	}
	
	public TimeoutException(Throwable cause) {
		super(cause);
	}
}
