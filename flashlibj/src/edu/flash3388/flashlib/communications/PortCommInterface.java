package edu.flash3388.flashlib.communications;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An abstract logic for IO port {@link CommInterface}s. Extends {@link StreamCommInterface} and adds manual connection
 * tracking to insure the existence of connection. Does not provide any data recovery. 
 * <p>
 * Connection is insured by checking the traffic going through the port. If data was not sent in a while, an handshake will
 * be sent instead. If data was not received for a while a timeout will occur. Once a defined amount of timeouts have occurred,
 * the connection will be considered lost.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class PortCommInterface extends StreamCommInterface{
	
	private boolean isConnected = false;
	private int lastRead = -1, timeLastTimeout = -1, lastSend = -1;
	private int connectionTimeout = CONNECTION_TIMEOUT, timeouts = 0, maxTimeouts = 3;
	
	/**
	 * Creates a new CommInterface for IO ports. Data is passed to the super class {@link StreamCommInterface}.
	 * <p>
	 * The default time in milliseconds for a timeout to occur is {@link CommInterface#CONNECTION_TIMEOUT}. 
	 * The default amount of timeouts for connection loss is 3.
	 * </p>
	 * 
	 * @param server indicates whether this interface initiates connection - client or waits for connection - server
	 * @param crcallow if true, data will be processed with CRC32 to dump data corruption
	 */
	public PortCommInterface(boolean server, boolean crcallow){
		super(server, crcallow);
	}
	/**
	 * Creates a new CommInterface for IO ports. Data is passed to the super class {@link StreamCommInterface}.
	 * CRC32 is initialized through this constructor. To control CRC usage, use {@link #PortCommInterface(boolean, boolean)}.
	 * <p>
	 * The default time in milliseconds for a timeout to occur is {@link CommInterface#CONNECTION_TIMEOUT}. 
	 * The default amount of timeouts for connection loss is 3.
	 * </p>
	 * 
	 * @param server indicates whether this interface initiates connection - client or waits for connection - server
	 */
	public PortCommInterface(boolean server){
		super(server);
	}
	
	private void writeHandshake(){
		write(HANDSHAKE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect(Packet packet) {
		timeouts = 0;
		lastSend = -1;
		lastRead = -1;
		isConnected = isBoundAsServer()? CommInterface.handshakeServer(this, packet) : 
			CommInterface.handshakeClient(this, packet);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect() {
		isConnected = false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean read(Packet packet) {
		boolean ret = super.read(packet);
		if(packet.length >= 0)
			lastRead = FlashUtil.millisInt();
		return ret;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data) {
		lastSend = FlashUtil.millisInt();
		super.write(data);
	}

	/**
	 * {@inheritDoc}
	 * Checks for timeouts in connection. If such events occur than connection is declared as lost.
	 */
	@Override
	public void update(int millis) {
		if(millis - lastRead >= connectionTimeout){
			timeouts++;
			lastRead = millis;
			timeLastTimeout = millis;
			FlashUtil.getLog().log("Timeout");
		}
		if(timeouts >= maxTimeouts){
			FlashUtil.getLog().log("Max timeouts");
			isConnected = false;
		}
		if(timeouts > 0 && timeLastTimeout != -1 && 
				millis - timeLastTimeout > (connectionTimeout*3)){
			timeouts = 0;
			timeLastTimeout = -1;
			FlashUtil.getLog().log("Timeout reset");
		}
		if(millis - lastSend >= connectionTimeout / 2)
			writeHandshake();
	}

	/**
	 * Sets the amount of timeout events to occur before declaring loss of connection.
	 * 
	 * @param timeouts amount of timeout events for connection lost
	 */
	public void setMaxTimeoutsCount(int timeouts){
		maxTimeouts = timeouts;
	}
	/**
	 * Gets the amount of timeout events needed for declaration of connection loss.
	 * @return amount of timeout events for connection lost
	 */
	public int getMaxTimeoutsCount(){
		return maxTimeouts;
	}
	
	/**
	 * Sets the time in milliseconds to pass without any data received for a timeout event to occur.
	 * 
	 * @param timeout time in milliseconds of lack of data for a timeout event to occur.
	 */
	public void setConnectionTimeout(int timeout){
		connectionTimeout = timeout;
	}
	/**
	 * Gets the time in milliseconds to pass without any data received for a timeout event to occur.
	 * @return time in milliseconds of lack of data for a timeout event to occur.
	 */
	public int getConnectionTimeout(){
		return connectionTimeout;
	}
}
