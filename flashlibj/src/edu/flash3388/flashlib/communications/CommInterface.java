package edu.flash3388.flashlib.communications;

public interface CommInterface {
	public static final int BUFFER_SIZE = 100;
	public static final int READ_TIMEOUT = 20;
	
	public static final byte[] HANDSHAKE = {0x01, 0xe, 0x07};
	public static final byte[] HANDSHAKE_CONNECT_SERVER = {0xb, 0x02, 0xa};
	public static final byte[] HANDSHAKE_CONNECT_CLIENT = {0xc, 0x10, 0x06};
	
	public static final int CONNECTION_TIMEOUT = 1500;
	
	void open();
	void close();
	void connect(Packet packet);
	void disconnect();
	boolean isConnected();
	boolean isOpened();
	boolean read(Packet packet);
	void setReadTimeout(long millis);
	long getTimeout();
	void setMaxBufferSize(int bytes);
	int getMaxBufferSize();
	void write(byte[] data);
	void write(byte[] data, int start, int length);
	void update(long millis);
}
