package edu.flash3388.flashlib.flashboard;

import java.io.IOException;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.CameraView;
import edu.flash3388.flashlib.communications.CameraServer;
import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.communications.UdpCommInterface;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.RemoteVision;

public class Flashboard {
	
	public static final int PORT_ROBOT = 5801;
	public static final int PORT_BOARD = 5800;
	public static final int CAMERA_PORT_ROBOT = 5803;
	public static final int CAMERA_PORT_BOARD = 5802;
	
	private static boolean instance = false, tcp = true;
	private static CameraView camViewer;
	private static CameraServer camServer;
	private static RemoteVision vision;
	private static Communications communications;
	
	public static void setProtocolTcp(){
		tcp = true;
	}
	public static void setProtocolUdp(){
		tcp = false;
	}
	public static boolean isProtocolTcp(){
		return tcp;
	}
	public static boolean isProtocolUdp(){
		return !tcp;
	}
	
	public static void attach(Sendable sendable){
		if(!instance) return;
		communications.attach(sendable);
	}
	public static void attach(Sendable... sendables){
		if(!instance) return;
		for (Sendable sendable : sendables) 
			communications.attach(sendable);
	}
	public static boolean detach(Sendable sendable){
		if(!instance) return false;
		return communications.detach(sendable);
	}
	public static boolean detach(int id){
		if(!instance) return false;
		return communications.detach(id);
	}
	public static Sendable getLocalByID(int id){
		if(!instance) return null;
		return communications.getLocalyAttachedByID(id);
	}
	
	public static boolean connect() throws IOException{
		if(!instance) return false;
		return communications.connect();
	}
	public static void disconnect(){
		if(!instance) return;
		communications.disconnect();
	}
	public static boolean isConnected(){
		if(!instance) return false;
		return communications.isConnected();
	}
	public static void start(){
		if(!instance) return;
		communications.start();
	}
	
	public static CameraView getCameraView(){
		return camViewer;
	}
	public static RemoteVision getVision(){
		return vision;
	}
	public static CameraServer getCameraServer(){
		return camServer;
	}
	
	public static void init(){
		init(tcp);
	}
	public static void init(boolean tcp){
		init(PORT_ROBOT, CAMERA_PORT_ROBOT, tcp);
	}
	public static void init(int port, int camport, boolean tcp){
		if(!instance){
			Flashboard.tcp = tcp;
			try {
				vision = new RemoteVision();
				camViewer = new CameraView("Flashboard-CamViewer", null, new Camera[]{});
				
				CommInterface readi;
				if(tcp)
					readi = new TcpCommInterface(port);
				else readi = new UdpCommInterface(port);
				
				communications = new Communications("Flashboard", readi);
				camServer = new CameraServer("Flashboard", camport, camViewer);
				communications.attach(vision);
				
				instance = true;
				FlashUtil.getLog().logTime("Flashboard: Initialized at port " + port);
			} catch (IOException e) {
				FlashUtil.getLog().reportError(e.getMessage());
			}
		}
	}
	public static boolean flashboardInit(){
		return instance;
	}
}
