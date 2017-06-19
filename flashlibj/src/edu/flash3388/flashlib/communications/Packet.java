package edu.flash3388.flashlib.communications;

import java.net.InetAddress;

/**
 * A structure for communications data read for a {@link CommInterface}.
 * All elements inside are public.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Packet {
	/**
	 * A received data buffer
	 */
	public byte[] data;
	/**
	 * The length of the data received
	 */
	public int length;
	
	/**
	 * The {@link InetAddress} object specifying the address of the sender
	 */
	public InetAddress senderAddress;
	/**
	 * The port of the sender
	 */
	public int senderPort;
}
