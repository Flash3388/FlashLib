package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;
import java.util.Arrays;

import edu.flash3388.flashlib.communications.PortCommInterface;
import edu.flash3388.flashlib.util.FlashUtil;
import io.silverspoon.bulldog.core.io.serial.SerialPort;

public class SbcSerialCommInterface extends PortCommInterface{
	
	private SerialPort port;
	private SbcReadCallable task;
	private int readtimeout = READ_TIMEOUT;
	
	public SbcSerialCommInterface(SerialPort port, boolean server) {
		super(server);
		this.port = port;
		port.setBlocking(true);
		task = new SbcReadCallable(port);
	}

	@Override
	public void open() {
		try {
			port.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void close() {
		try {
			port.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean isOpened() {
		return port.isOpen();
	}

	@Override
	public void setReadTimeout(int millis) {
		readtimeout = millis;
	}
	@Override
	public int getTimeout() {
		return readtimeout;
	}

	@Override
	protected void writeData(byte[] data, int start, int length) {
		try {
			port.writeBytes(Arrays.copyOfRange(data, start, length));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected int readData(byte[] buffer) {
		task.setBuffer(buffer);
		return FlashUtil.executeForTime(task, readtimeout);
	}

}
