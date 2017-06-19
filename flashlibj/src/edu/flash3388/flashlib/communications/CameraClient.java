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

/**
 * CameraClient provides a UDP communications client for camera data. It connects with a remote {@link CameraServer},
 * receives byte arrays holding image data and passes them on to attached {@link DataListener}s. Data reading runs in
 * a separate thread which starts immediately and can be stopped by calling {@link #stop()}.
 * <p>
 * To receive image data, implement {@link DataListener} and add it using {@link #addListener(DataListener)}. Data will be
 * passed automatically on arrival. But the data is passed as bytes, so the image will need to be created manually. This allows
 * for different image data encrypting.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
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
	
	/**
	 * The default max possible amount of bytes that can be read from the socket.
	 */
	public static final int DEFAULT_MAX_BYTES = (int) 1e5;
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
	private String name, logName;
	
	private boolean stop = false, connected = false;
	
	/**
	 * Creates a new CameraServer. A datagram socket is created at the local address listening to a given port. The read thread
	 * is immediately started and data is received. In order to initiate connection with the remote {@link CameraServer} a handshake
	 * is sent to a given port and address which belong to the remote camera server. To avoid to much data, the data buffer is
	 * limited to a maximum amount of bytes.
	 * 
	 * @param name the name of client, for logging
	 * @param localPort local port for data listening
	 * @param remoteAdd remote server address
	 * @param remotePort remote server port
	 * @param maxBytes maximum buffer size
	 */
	public CameraClient(String name, int localPort, InetAddress remoteAdd, int remotePort, int maxBytes){
		port = localPort;
		sendPort = remotePort;
		sendAddress = remoteAdd;
		this.name = name;
		logName = name+"-CameraClient";
		try {
			socket = new DatagramSocket(new InetSocketAddress(localPort));
			socket.setSoTimeout(READ_TIMEOUT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		recBytes = new byte[maxBytes];
		 
		runThread = new Thread(new Task(this));
		runThread.start();
	}
	/**
	 * Creates a new CameraServer. A datagram socket is created at the local address listening to a given port. The read thread
	 * is immediately started and data is received. In order to initiate connection with the remote {@link CameraServer} a handshake
	 * is sent to a given port and address which belong to the remote camera server. To avoid to much data, the data buffer is
	 * limited to a the default amount of bytes: 100000 bytes.
	 * 
	 * @param name the name of client, for logging
	 * @param localPort local port for data listening
	 * @param remoteAdd remote server address
	 * @param remotePort remote server port
	 */
	public CameraClient(String name, int localPort, InetAddress remoteAdd, int remotePort){
		this(name, localPort, remoteAdd, remotePort, DEFAULT_MAX_BYTES);
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
	
	/**
	 * Gets the local port being listened to for data by the datagram socket.
	 * @return the listening port
	 */
	public int getLocalPort(){
		return port;
	}
	/**
	 * Gets the remote server port to which data is sent.
	 * @return remote server port
	 */
	public int getRemotePort(){
		return sendPort;
	}
	/**
	 * Gets the address of the connected server as a {@link InetAddress} object.
	 * @return the address of the server
	 */
	public InetAddress getRemoteAddress(){
		return sendAddress;
	}
	
	/**
	 * Gets the maximum amount of bytes that can be read.
	 * @return max bytes 
	 */
	public int getMaxBytes(){
		return recBytes.length;
	}
	/**
	 * Sets the maximum amount of bytes that can be read by the client.
	 * @param maxbytes max bytes to be read
	 */
	public void setMaxBytes(int maxbytes){
		synchronized (recBytes) {
			recBytes = new byte[maxbytes];
		}
	}
	
	/**
	 * Gets whether or not the client is connected to a remote camera server
	 * @return true if connected, false otherwise
	 */
	public boolean isConnected(){
		return connected;
	}

	/**
	 * Gets the name of this client used for data logging.
	 * @return the name of this client
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Stops the operation of the client. Closes the socket and waits for termination of the reading thread.
	 */
	public void stop(){
		stop = true;
		connected = false;
		socket.close();
		
		try {
			runThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a new {@link DataListener} to this client. When camera data is read, it will be passed to this listener.
	 * 
	 * @param listener data listener to add
	 */
	public void addListener(DataListener listener){
		listeners.addElement(listener);
	}
	/**
	 * Removes a {@link DataListener} from this client. The listener will no longer receive image data.
	 * @param listener data listener to remove
	 */
	public void removeListener(DataListener listener){
		listeners.removeElement(listener);
	}
}
