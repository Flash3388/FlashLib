package edu.flash3388.flashlib.communications;

public interface ManualCommInterface extends CommInterface{
	public static final byte[] HANDSHAKE = {0x01, 0xe, 0x07};
	public static final byte[] HANDSHAKE_CONNECT_SERVER = {0xb, 0x02, 0xa};
	public static final byte[] HANDSHAKE_CONNECT_CLIENT = {0xc, 0x10, 0x06};
	
	public static final int CONNECTION_TIMEOUT = 1500;
}
