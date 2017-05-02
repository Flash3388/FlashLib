package edu.flash3388.flashlib.communications;

import java.net.InetAddress;

public class Packet {
	public byte[] data;
	public int length;
	public InetAddress senderAddress;
	public int senderPort;
}
