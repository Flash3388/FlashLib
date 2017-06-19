package edu.flash3388.flashlib.robot.sbc;

import java.util.concurrent.Callable;

import io.silverspoon.bulldog.core.io.IOPort;

public class SbcReadCallable implements Callable<Integer>{

	private IOPort port;
	private byte[] buffer;
	
	public SbcReadCallable(IOPort port){
		this.port = port;
	}
	
	public void setBuffer(byte[] buffer){
		this.buffer = buffer;
	}
	
	@Override
	public Integer call() throws Exception {
		if(buffer == null)
			return -1;
		return port.readBytes(buffer);
	}
}
