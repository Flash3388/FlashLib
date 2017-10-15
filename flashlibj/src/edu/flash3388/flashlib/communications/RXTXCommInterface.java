package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * 
 * Manages a serial communications port using the RXTX communications library. 
 * Extends {@link SerialCommInterface}, allowing for data management and connection management. Does
 * not provide any data loss/corruption handling.
 * 
 * @author Tom Tzook
 * @author Alon Klein
 * @since FlashLib 1.0.1
 */
public class RXTXCommInterface extends SerialCommInterface {

	private String portName;
	private int baudrate;
	private int timeout;
	
	private InputStream in;
	private OutputStream out;
	
	private SerialPort serial;
	
	private boolean isOpened;
	
	public RXTXCommInterface(boolean server, String port) {
		this(server, port, 100);
	}
	public RXTXCommInterface(boolean server, String port, int timeout) {
		this(server, port, timeout, 57600);
	}
	public RXTXCommInterface(boolean server, String port, int timeout, int baudrate) {
		super(server, false);
		portName = port;
		this.timeout = timeout;
		this.baudrate = baudrate;
		isOpened = false;
	}


	@Override
	public void open() {
		try{
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
	        if (portIdentifier.isCurrentlyOwned())
	            System.out.println("Error: Port is currently in use");
	        else {
	            CommPort port = portIdentifier.open(this.getClass().getName(), getTimeout());//for now class name
	            
	            if (port instanceof SerialPort){
	            	serial = (SerialPort) port;
	            	serial.setSerialPortParams(baudrate, SerialPort.DATABITS_8 
	                		, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	                
	            	serial.enableReceiveTimeout(timeout);
	            	
	                this.in = serial.getInputStream();
	                this.out = serial.getOutputStream();
	                isOpened = true;
	            }
	            else
	                System.out.println("Error: Only serial ports are handled");
	        }   
		}
		catch (PortInUseException | UnsupportedCommOperationException | NoSuchPortException | IOException e) {
			if(serial != null)
				serial.close();
			serial = null;
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			out.close();
			in.close();
			serial.close();
			serial = null;
			isOpened = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isOpened() {
		return isOpened;
	}

	@Override
	public void setReadTimeout(int millis) {
		timeout = millis;
		if(serial != null){
			try {
				serial.enableReceiveTimeout(millis);
			} catch (UnsupportedCommOperationException e) {
			}
		}
	}
	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	protected void writeData(byte[] data, int start, int length) {
		try {
			out.write(data, start, length);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	@Override
	protected int readData(byte[] buffer) {
		try {
			return in.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Gets the name of the serial port used in this communication interface.
	 * @return the name of the communications port.
	 */
	public String getPortName(){
		return portName;
	}
	
	/**
	 * Gets the data rate used by this port in Bits per second.
	 * @return data rate in bits per second.
	 */
	public int getBaudrate(){
		return baudrate;
	}
	/**
	 * Sets the data rate to be used when the port is opened. If the port is already opened, nothing will occur.
	 * @param bRate data rate in bits per second
	 */
	public void setBaudrate(int bRate){
		if(isOpened())
			return;
		baudrate = bRate;
	}
}
