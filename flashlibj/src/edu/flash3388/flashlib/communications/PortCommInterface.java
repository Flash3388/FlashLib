package edu.flash3388.flashlib.communications;

import edu.flash3388.flashlib.util.FlashUtil;

public abstract class PortCommInterface extends StreamCommInterface{
	
	private boolean isConnected = false;
	private long lastRead = -1, timeLastTimeout = -1;
	private int connectionTimeout = CONNECTION_TIMEOUT, timeouts = 0, maxTimeouts = 3;
	
	public PortCommInterface(boolean server, boolean crcallow){
		super(server, crcallow);
	}
	public PortCommInterface(boolean server){
		super(server);
	}
	
	private void writeHandshake(){
		write(HANDSHAKE);
	}
	
	@Override
	public void connect(Packet packet) {
		timeouts = 0;
		isConnected = isBoundAsServer()? Communications.handshakeServer(this, packet) : 
			Communications.handshakeClient(this, packet);
	}
	@Override
	public void disconnect() {
		isConnected = false;
	}
	@Override
	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public boolean read(Packet packet) {
		boolean ret = super.read(packet);
		if(packet.length >= 0)
			lastRead = FlashUtil.millis();
		return ret;
	}

	@Override
	public void update(long millis) {
		if(millis - lastRead >= connectionTimeout){
			timeouts++;
			lastRead = millis;
			timeLastTimeout = millis;
			System.out.println("Timeout");
		}
		if(timeouts >= maxTimeouts){
			System.out.println("Max timeouts");
			isConnected = false;
		}
		if(timeouts > 0 && timeLastTimeout != -1 && 
				millis - timeLastTimeout > (connectionTimeout*3)){
			timeouts = 0;
			timeLastTimeout = -1;
			System.out.println("Timeout reset");
		}
		writeHandshake();
	}

	public void setMaxTimeoutsCount(int timeouts){
		maxTimeouts = timeouts;
	}
	public int getMaxTimeoutsCount(){
		return maxTimeouts;
	}
	public void setConnectionTimeout(int timeout){
		connectionTimeout = timeout;
	}
	public int getConnectionTimeout(){
		return connectionTimeout;
	}
}
