package edu.flash3388.flashlib.communications.message;

public class WriteException extends Exception {

	public WriteException(String message) {
		super(message);
	}
	
	public WriteException(Throwable cause) {
		super(cause);
	}
}
