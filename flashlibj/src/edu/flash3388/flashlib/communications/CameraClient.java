package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;


public class CameraClient {
	private static class Task implements Runnable{
		CameraClient client;
		
		public Task(CameraClient client){
			this.client = client;
		}
		
		@Override
		public void run() {
			FlashUtil.getLog().log("Running", client.logName);
			while(!client.stop){
				if(!client.connected)
					client.write(HANDSHAKE);
				client.read();
				FlashUtil.delay(5);
			}
		}
	}
	
	public static final int DEFUALT_MAX_BYTES = (int) 1e5;
	private static final byte[] HANDSHAKE = {0x01, 0x00, 0x01};
	private static final int READ_TIMEOUT = 800;
	
	private Vector<DataListener> listeners = new Vector<DataListener>(5);
	private Thread runThread;
	private DatagramSocket socket;
	private byte[] recBytes;
	private DatagramPacket recPacket;
	
	private InetAddress sendAddress;
	private int sendPort;
	private int port;
	private int maxBytes;
	private String name, logName;
	
	private boolean stop = false, connected = false;
	
	public CameraClient(String name, int localPort, InetAddress remoteAdd, int remotePort, int maxBytes){
		port = localPort;
		sendPort = remotePort;
		sendAddress = remoteAdd;
		this.maxBytes = maxBytes;
		this.name = name;
		logName = name+"-CameraClient";
		try {
			socket = new DatagramSocket(new InetSocketAddress(localPort));
			socket.setSoTimeout(READ_TIMEOUT);
		} catch (SocketException e) {
		}
		recBytes = new byte[maxBytes];
		recPacket = new DatagramPacket(recBytes, maxBytes);
		 
		runThread = new Thread(new Task(this));
		runThread.start();
	}
	
	private void write(byte[] bytes){
		try {
			socket.send(new DatagramPacket(bytes, bytes.length, sendAddress, sendPort));
		} catch (IOException e) {
		}
	}
	private void read(){
		try {
			recPacket = new DatagramPacket(recBytes, recBytes.length);
			socket.receive(recPacket);
			int len = recPacket.getLength();
			
			if(!connected) {
				connected = true;
				FlashUtil.getLog().log("Connection Established", logName);
			}
			if(len <= 10){
				write(HANDSHAKE);
				return;
			}
			
			if(listeners.size() > 0){
				byte[] data = Arrays.copyOfRange(recBytes, 0, len);
				for (Enumeration<DataListener> lEnum = listeners.elements(); lEnum.hasMoreElements();) {
					DataListener listener = lEnum.nextElement();
					if(listener != null)
						listener.newData(data);
				}
					
			}
		} catch(SocketTimeoutException e1){
			if(connected){
				connected = false;
				FlashUtil.getLog().log("Connection Lost", logName);
			}
		}catch (IOException | NumberFormatException e) {
		}
	}
	
	public int getLocalPort(){
		return port;
	}
	public int getRemotePort(){
		return sendPort;
	}
	public int getMaxBytes(){
		return maxBytes;
	}
	public boolean isConnected(){
		return connected;
	}
	public void setMaxBytes(int maxbytes){
		maxBytes = maxbytes;
	}
	public String getName(){
		return name;
	}
	public void stop(){
		stop = true;
		connected = false;
		socket.close();
	}
	
	public void addListener(DataListener listener){
		listeners.addElement(listener);
	}
	public void removeListener(DataListener listener){
		listeners.removeElement(listener);
	}
}
