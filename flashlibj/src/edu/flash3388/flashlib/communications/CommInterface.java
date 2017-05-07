package edu.flash3388.flashlib.communications;

public interface CommInterface {
	public static final int BUFFER_SIZE = 100;
	public static final int READ_TIMEOUT = 20;
	
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
