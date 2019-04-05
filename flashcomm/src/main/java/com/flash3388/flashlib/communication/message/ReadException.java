package com.flash3388.flashlib.communication.message;

public class ReadException extends Exception {

	public ReadException(String message) {
		super(message);
	}
	
	public ReadException(Throwable cause) {
		super(cause);
	}
}
