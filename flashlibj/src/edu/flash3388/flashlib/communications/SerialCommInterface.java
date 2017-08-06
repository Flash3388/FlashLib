package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.*;

public class SerialCommInterface extends PortCommInterface {

	
	private String portName;
	private int baudrate;
	private int timeout;
	private InputStream in;
	private OutputStream out;
	
	private CommPort commPort;
	
	private boolean isOpened;
	
	public SerialCommInterface(boolean server, String port) {
		this(server,port,100);
	}
	
	public SerialCommInterface(boolean server, String port,int timeout) {
		this(server,port,timeout,57600);
	}
	
	
	public SerialCommInterface(boolean server, String port,int timeout,int baudrate) {
		super(server, false);
		portName = port;
		this.timeout = timeout;
		this.baudrate = baudrate;
		isOpened = false;
	}


	@Override
	public void open() {
		try
		{
			
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
	        if ( portIdentifier.isCurrentlyOwned() )
	            System.out.println("Error: Port is currently in use");
	        else
	        {
	            commPort = portIdentifier.open(this.getClass().getName(),getTimeout());//for now class name
	            
	            if ( commPort instanceof SerialPort )
	            {
	                SerialPort serialPort = (SerialPort) commPort;
	                serialPort.setSerialPortParams(baudrate,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
	                
	                this.in = serialPort.getInputStream();
	                this.out = serialPort.getOutputStream();
	                isOpened = true;
	            }
	            else
	                System.out.println("Error: Only serial ports are handled");
	        }   
		}
		catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			out.close();
			in.close();
			((SerialPort)commPort).close();
			isOpened = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	protected void writeData(byte[] data, int start, int length) {
		try {
			out.write(data,start,length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	

	@Override
	protected int readData(byte[] buffer) {
		
		int len = 0;
		try {
			len = in.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return len;
	}
	
	public String getPortName()
	{
		return portName;
	}
	
	public int getBaudrate()
	{
		return baudrate;
	}
	
	public void setBaudrate(int bRate)
	{
		baudrate = bRate;
	}

	

}
