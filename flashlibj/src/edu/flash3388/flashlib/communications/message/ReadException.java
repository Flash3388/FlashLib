package edu.flash3388.flashlib.communications.message;

public class ReadException extends Exception {

	public ReadException(String message) {
		super(message);
	}
	
	public ReadException(Throwable cause) {
		super(cause);
	}
}
