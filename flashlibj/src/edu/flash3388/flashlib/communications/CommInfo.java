package edu.flash3388.flashlib.communications;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class CommInfo {
	
	public static final String BEAGLEBONE_HOST = "beaglebone-3388.local";
	public static final String RASPBERRYPI_HOST = "raspberrypi-3388.local";
	public static final String ROBORIO_HOST = "roborio-3388-frc.local";
	
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
