package edu.flash3388.flashlib.communications;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class CommInfo {
	public static final int FLASHBOARD_PORT_ROBORIO = 5801;
	public static final int FLASHBOARD_PORT_BOARD = 5800;
	public static final int FLASHBOARD_CAMERA_PORT_ROBORIO = 5803;
	public static final int FLASHBOARD_CAMERA_PORT_BOARD = 5802;
	
	public static final String BEAGLEBONE_HOST = "beaglebone-3388.local";
	public static final String RASPBERRYPI_HOST = "raspberrypi-3388.local";
	public static final String ROBORIO_HOST = "roborio-3388-frc.local";
	
	public static final int ROBORIO2RASPBERRY_PORT_ROBORIO = 8000;
	public static final int ROBORIO2RASPBERRY_PORT_RASPBERRY = 8001;
	public static final int ROBORIO2RASPBERRY_PORT_ROBORIO_CAMERA = 8015;
	
	public static final int ROBORIO2BEAGLEBONE_PORT_ROBORIO = 9000;
	public static final int ROBORIO2BEAGLEBONE_PORT_BEAGLEBONE = 9001;
	public static final int ROBORIO2BEAGLEBONE_PORT_ROBORIO_CAMERA = 9015;
	
	public final String hostname;
	public final int localPort;
	public final int remotePort;
	public final int camPort;
	
	public CommInfo(String host, int local, int remote, int camPort){
		this.hostname = host;
		this.localPort = local;
		this.remotePort = remote;
		this.camPort = camPort;
	}
	
	public static final CommInfo Roborio2Flashboard = new CommInfo(ROBORIO_HOST, FLASHBOARD_PORT_ROBORIO, -1, FLASHBOARD_CAMERA_PORT_ROBORIO);
	
	public static final CommInfo Roborio2Beaglebone = new CommInfo(BEAGLEBONE_HOST, ROBORIO2BEAGLEBONE_PORT_ROBORIO, -1, ROBORIO2BEAGLEBONE_PORT_ROBORIO_CAMERA);
	public static final CommInfo Roborio2Raspberry = new CommInfo(RASPBERRYPI_HOST, ROBORIO2RASPBERRY_PORT_ROBORIO, -1, ROBORIO2RASPBERRY_PORT_ROBORIO_CAMERA);
	
	public static final CommInfo Beaglebone2Roborio = new CommInfo(ROBORIO_HOST, ROBORIO2BEAGLEBONE_PORT_BEAGLEBONE, ROBORIO2BEAGLEBONE_PORT_ROBORIO, -1);
	public static final CommInfo Raspberry2Roborio = new CommInfo(ROBORIO_HOST, ROBORIO2RASPBERRY_PORT_RASPBERRY, ROBORIO2RASPBERRY_PORT_ROBORIO, -1);

	public static InetAddress getInterfaceAddress(InetAddress remote) throws SocketException{
		byte[] remoteAddrByte = remote.getAddress();
		Enumeration<NetworkInterface> interEnum = NetworkInterface.getNetworkInterfaces();
		
		while(interEnum.hasMoreElements()){
			NetworkInterface inter = interEnum.nextElement();
			List<InterfaceAddress> addresses = inter.getInterfaceAddresses();
			for (int i = 0; i < addresses.size(); i++) {
				InterfaceAddress address = addresses.get(i);
				byte[] byteAddr = address.getAddress().getAddress();
				
				if(byteAddr.length != remoteAddrByte.length)
					continue;
				
				boolean error = false;
				for(int j = 0; j < remoteAddrByte.length - 1; j++){
					if(byteAddr[j] != remoteAddrByte[j]){
						error = true;
						break;
					}
				}
				if(!error)
					return address.getAddress();
			}
		}
		return null;
	}
}
