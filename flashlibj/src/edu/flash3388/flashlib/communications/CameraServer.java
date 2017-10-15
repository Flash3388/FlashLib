package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * CameraClient provides a UDP communications server for camera data. A remote {@link CameraClient} can connect to it by 
 * sending an handshake; upon such event, camera data is read from a defined camera by calling {@link Camera#getData()} 
 * and sent to the client. The sending interval is defined by the defined camera FPS. If the defined FPS is not valid,
 * the default 30 FPS value is used instead. Data is sent and received in a 
 * separate thread which can be stopped by calling {@link #close()}.
 * <p>
 * Every 3 seconds, the server performs an handshake check to make sure the connection still exists. If it does not, the server
 * waits for a new connection.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class CameraServer {
	private static class Task implements Runnable{
		CameraServer server;
		
		public Task(CameraServer server){
			this.server = server;
		}
		
		@Override
		public void run() {
			try {
				byte bytes[] = new byte[1];
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				server.socket.receive(packet);
				server.sendAddress = packet.getAddress();
				server.sendPort = packet.getPort();
				
				FlashUtil.getLog().log("Client Connected: "+server.sendAddress.getHostAddress()+
						":"+server.sendPort, server.logName);
				
				byte[] checkBytes = HANDSHAKE;
				int cmillis = FlashUtil.millisInt();
				int period = (server.camera == null || server.camera.getFPS() <= 5? DEFAULT_PERIOD : 
					1000 / server.camera.getFPS()), 
						lastCheck = cmillis;
				server.lastCalVal = cmillis;
				while(!server.stop){
					int t0 = cmillis;
					
					if(server.camera == null) {
						FlashUtil.delay(100);
						continue;
					}
					byte[] imageArray = server.camera.getData();
					if(imageArray == null) {
						FlashUtil.delay(100);
						continue;
					}
			        
			        server.socket.send(new DatagramPacket(imageArray, imageArray.length, server.sendAddress, server.sendPort));
			        server.bytesSent += imageArray.length;
			        
			        cmillis = FlashUtil.millisInt();
			        int dt = cmillis - t0;

		            if (dt > 0 && dt < period)
		            	FlashUtil.delay(dt);
		            
		            cmillis = FlashUtil.millisInt();
		            if(cmillis - lastCheck > CHECK_PERIOD){
		            	server.socket.send(new DatagramPacket(checkBytes, checkBytes.length, server.sendAddress, server.sendPort));
				       
		            	int port = server.sendPort;
		            	String address = server.sendAddress.getHostAddress();
				        packet = new DatagramPacket(bytes, bytes.length);
						server.socket.receive(packet);
						server.sendAddress = packet.getAddress();
						server.sendPort = packet.getPort();
						
						String naddress = server.sendAddress.getHostAddress();
						if(port != server.sendPort || !address.equals(naddress))
							FlashUtil.getLog().log("Client Connected: "+naddress+":"+
									server.sendPort, server.logName);
						
		            	lastCheck = cmillis;
		            }
		            cmillis = FlashUtil.millisInt();
				}
			} catch (IOException e) {
				FlashUtil.getLog().reportError(e.getMessage());
			}
		}
	}
	
	private static final int CHECK_PERIOD = 3000;
	private static final int DEFAULT_PERIOD = (int) (1000 / 30.0);
	private static final byte[] HANDSHAKE = {0x01, 0x00, 0x01};
	
	private Thread runThread;
	private DatagramSocket socket;
	
	private InetAddress sendAddress;
	private int sendPort;
	private int port;
	private String logName;
	private long bytesSent = 0;
	private long lastCalVal = 0;
	
	private Camera camera;
	private boolean stop = false;
	
	/**
	 * Creates a new CameraServer. A datagram socket is created and listens for incoming data on a specific port. A data
	 * reading and sending thread is created and started immediately. Upon handshake initiation from a client, data from
	 * the defined camera is sent by calling the {@link Camera#getData()} method for bytes and sending them using the
	 * datagram socket.
	 * 
	 * @param name for logging data
	 * @param localPort port to listen for incoming data
	 * @param camera camera whose data is sent
	 */
	public CameraServer(String name, int localPort, Camera camera){
		port = localPort;
		logName = name+"-CameraServer";
		try {
			socket = new DatagramSocket(new InetSocketAddress(localPort));
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		this.camera = camera;
		runThread = new Thread(new Task(this));
		runThread.start();
	}
	/**
	 * Creates a new CameraServer. A datagram socket is created and listens for incoming data on a specific port. A data
	 * reading and sending thread is created and started immediately. Upon handshake initiation from a client, data from
	 * the defined camera is sent by calling the {@link Camera#getData()} method for bytes and sending them using the
	 * datagram socket.
	 * <p>
	 * A default logging name is used for data logging: CameraServer
	 * </p>
	 * 
	 * @param localPort port to listen for incoming data
	 * @param camera camera whose data is sent
	 */
	public CameraServer(int localPort, Camera camera){
		this("CamServer", localPort, camera);
	}
	
	/**
	 * Gets the logging name used by the thread for data logging.
	 * @return the logging name
	 */
	public String getName(){
		return logName;
	}
	
	/**
	 * Gets the local port to which the datagram sockets listents to for data.
	 * @return local data port
	 */
	public int getLocalPort(){
		return port;
	}
	/**
	 * Gets the port of the connected client.
	 * @return port of the connected client
	 */
	public int getRemotePort(){
		return sendPort;
	}
	/**
	 * Gets the address of the connected client as a {@link InetAddress} object.
	 * @return the address of the client
	 */
	public InetAddress getRemoteAddress(){
		return sendAddress;
	}
	
	/**
	 * Gets the {@link Camera} object which provides the image data.
	 * @return the camera
	 */
	public Camera getCamera(){
		return camera;
	}
	
	/**
	 * Stops the operation of the server. Closes the socket and waits for termination of the reading thread.
	 */
	public void close(){
		stop = true;
		socket.close();
		
		try {
			runThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the bandwidth usage of the socket in Mbps
	 * @return bandwidth usage in Mbps
	 */
	public double getBandwidthUsage(){
		long cMillis = FlashUtil.millis();
		double secs = (cMillis - lastCalVal) / 1000.0;
		double mbytes = bytesSent * 8 / 1e6;
		bytesSent = 0;
		lastCalVal = cMillis;
		return mbytes / secs;
	}
}
