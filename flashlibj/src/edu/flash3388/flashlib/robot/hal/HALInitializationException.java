package edu.flash3388.flashlib.robot.hal;

public class HALInitializationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int port;
	
	public HALInitializationException(String msg, int port) {
		super(msg + " :: Port = "+port);
		this.port = port;
	}
	
	public int getPort(){
		return port;
	}
}
