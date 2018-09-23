package edu.flash3388.flashlib.util;

import edu.flash3388.flashlib.util.beans.BooleanSource;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

/**
 * FlashUtil contains utility functions used throughout flashLib.
 * <p>
 * The utilities are divided into types:
 * <ul>
 * 		<li> Time utilities: Provides time stamp data and delay</li>
 * 		<li> Array utilities: Array shifting, copying, enlarging, printing, etc</li>
 * 		<li> Type conversion utilities: Converting between bytes, Strings and other data types </li>
 * 		<li> Parsing utilities: Parsing lines of String for data </li>
 * 		<li> Reflection utilities: Java reflection assistance </li>
 * 		<li> Network utilities: Getting network addresses and interfaces </li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public final class FlashUtil {
	
	/**
	 * The current version of FlashLib.
	 */
	public static final String VERSION = "1.3.0";
	
	private FlashUtil(){}
	
	private static final String os = System.getProperty("os.name").toLowerCase();
	private static final String architecture = System.getProperty("os.arch");
	private static final String version = System.getProperty("os.version");

	
	//--------------------------------------------------------------------
	//----------------------Communications--------------------------------
	//--------------------------------------------------------------------
	
    /**
     * Gets the local {@link java.net.InetAddress} of the network interface connected to a give remote address.
     * 
     * @param remote the remote address
     * @return the local {@link java.net.InetAddress} of the network interface
     * @throws SocketException if an I/O error occurs.
     */
	public static InetAddress getLocalAddress(InetAddress remote) throws SocketException{
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
	
	//--------------------------------------------------------------------
	//------------------------------OS------------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets whether or not the current operating system is windows.
	 * @return true if the current OS is windows.
	 */
	public static boolean isWindows(){
		return os.indexOf("win") >= 0;
	}
	/**
	 * Gets whether or not the current operating system is unix or linux.
	 * @return true if the current OS is windows.
	 */
	public static boolean isUnix(){
		return os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0;
	}

	/**
	 * Gets whether or not the current operating system architecture is 64bit.
	 * @return true if the current OS architecture is 64bit.
	 */
	public static boolean isArchitectureX64(){
		return architecture.indexOf("64") >= 0;
	}
}
