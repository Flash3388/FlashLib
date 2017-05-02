package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.util.FlashUtil;

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
				long cmillis = FlashUtil.millis();
				long period = (server.camera == null || server.camera.getFPS() <= 5? DEFAULT_PERIOD : 
					1000 / server.camera.getFPS()), 
						lastCheck = cmillis;
				server.lastCalVal = cmillis;
				while(!server.stop){
					long t0 = cmillis;
					
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
			        
			        cmillis = FlashUtil.millis();
			        long dt = cmillis - t0;

		            if (dt < period)
		            	FlashUtil.delay(dt);
		            
		            cmillis = FlashUtil.millis();
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
		            cmillis = FlashUtil.millis();
				}
			} catch (IOException e) {
				FlashUtil.getLog().reportError(e.getMessage());
			}
		}
	}
	
	private static final long CHECK_PERIOD = 3000;
	private static final long DEFAULT_PERIOD = (long) (1000 / 30.0);
	private static final byte[] HANDSHAKE = {0x01, 0x00, 0x01};
	
	private Thread runThread;
	private DatagramSocket socket;
	
	private InetAddress sendAddress;
	private int sendPort;
	private int port;
	private String name, logName;
	private long bytesSent = 0;
	private long lastCalVal = 0;
	
	private Camera camera;
	private boolean stop = false;
	
	public CameraServer(String name, int localPort, Camera camera){
		port = localPort;
		this.name = name;
		logName = name+"-CameraServer";
		try {
			socket = new DatagramSocket(new InetSocketAddress(localPort));
		} catch (SocketException e) {
		}
		
		this.camera = camera;
		runThread = new Thread(new Task(this));
		runThread.start();
	}
	public CameraServer(int localPort, Camera camera){
		this("CamServer", localPort, camera);
	}
	
	public String getName(){
		return name;
	}
	public int getLocalPort(){
		return port;
	}
	public int getRemotePort(){
		return sendPort;
	}
	public InetAddress getRemoteAddress(){
		return sendAddress;
	}
	public Camera getCamera(){
		return camera;
	}
	public void stop(){
		stop = true;
		socket.close();
	}
	public double getBandwidthUsage(){
		long cMillis = FlashUtil.millis();
		double secs = (cMillis - lastCalVal) / 1000.0;
		double mbytes = bytesSent * 8 / 1e6;
		bytesSent = 0;
		lastCalVal = cMillis;
		return mbytes / secs;
	}
}
