package edu.flash3388.flashlib.robot.hal;

public class HALInitialzationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int port;
	
	public HALInitialzationException(String msg, int port) {
		super(msg + " :: Port = "+port);
		this.port = port;
	}
	
	public int getPort(){
		return port;
	}
}
