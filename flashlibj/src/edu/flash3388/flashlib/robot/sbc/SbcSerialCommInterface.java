package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;

import edu.flash3388.flashlib.communications.BusCommInterface;
import edu.flash3388.flashlib.util.FlashUtil;
import io.silverspoon.bulldog.core.io.serial.SerialPort;

public class SbcSerialCommInterface extends BusCommInterface{
	
	private SerialPort port;
	private SbcReadCallable task;
	private long readtimeout = READ_TIMEOUT;
	
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
	public void setReadTimeout(long millis) {
		readtimeout = millis;
	}
	@Override
	public long getTimeout() {
		return readtimeout;
	}

	@Override
	protected void writePort(byte[] data) {
		try {
			port.writeBytes(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected int readPort(byte[] buffer) {
		task.setBuffer(buffer);
		return FlashUtil.executeForTime(task, readtimeout);
	}

}
