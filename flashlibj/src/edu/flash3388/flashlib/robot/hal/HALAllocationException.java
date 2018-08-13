package edu.flash3388.flashlib.robot.hal;

public class HALAllocationException extends HALInitializationException {
	
	private static final long serialVersionUID = 1L;

	public HALAllocationException(String msg, int port) {
		super(msg, port);
	}
}
