package edu.flash3388.flashlib.robot;

import java.io.InputStream;
import java.io.OutputStream;

public interface Shell {

	public static final byte EXECUTE_PROGRESS = 0x07;
	public static final byte EXECUTE_IDLE = 0x00;
	
	static final byte OUTPUT_STREAM_UPDATE = 0x05;
	static final byte INPUT_STREAM_UPDATE = 0x03;
	static final byte EXECUTE_CODE_UPDATE = 0xe;
	static final byte EXECUTE_START = 0x01;
	static final byte EXECUTE_STOP = 0xa;
	
	boolean execute(String command);
	boolean isExecuting();
	int getExecutionCode();
	void stopExecution();
	
	OutputStream getOutputStream();
	InputStream getInputStream();
}
