package com.flash3388.flashlib.communication.message;

public class WriteException extends Exception {

	public WriteException(String message) {
		super(message);
	}
	
	public WriteException(Throwable cause) {
		super(cause);
	}
}
