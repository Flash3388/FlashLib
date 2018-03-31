package edu.flash3388.flashlib.communications;

import java.io.IOException;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An abstract CommInterface which handles manual connection verification, allowing to insure the existance of connections
 * by data sending.
 * <p>
 * Connection is insured by checking the traffic going through the port. If data was not sent in a while, an handshake will
 * be sent instead. If data was not received for a while a timeout will occur. Once a defined amount of timeouts have occurred,
 * the connection will be considered lost.
 * </p>
 * <p>
 * When implementing, it is necessary to call {@link #newDataRead()} when new data was read from remote, {@link #newDataSent()}
 * when new data was sent to the remote and {@link #resetData()} when connection was established with the remote.
 * {@link #update(int)} must be called periodically to test connection. This is done automatically if using {@link Communications}
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public abstract class ManualConnectionVerifier implements CommInterface {

	/**
	 * An handshake byte array. Used by {@link UDPCommInterface} and {@link SerialCommInterface}
	 */
	public static final byte[] HANDSHAKE = {0x01, 0xe, 0x07};
	/**
	 * A server connection handshake. Used by {@link UDPCommInterface} and {@link SerialCommInterface}
	 */
	public static final byte[] HANDSHAKE_CONNECT_SERVER = {0xb, 0x02, 0xa};
	/**
	 * A client connection handshake. Used by {@link UDPCommInterface} and {@link SerialCommInterface}
	 */
	public static final byte[] HANDSHAKE_CONNECT_CLIENT = {0xc, 0x10, 0x06};
	
	/**
	 * A default connection timeout for protocols which need to manually check for connection, like: UDP.
	 */
	public static final int CONNECTION_TIMEOUT = 1000;
	
	private int lastRead = -1, timeLastTimeout = -1, lastSend = -1;
	private int connectionTimeout = CONNECTION_TIMEOUT, timeouts = 0, maxTimeouts = 3;
	private boolean verifyConnection = true;
	
	private void writeHandshake() throws IOException {
		write(HANDSHAKE);
	}
	
	/**
	 * {@inheritDoc}
	 * Checks for timeouts in connection. If such events occur than connection is declared as lost and
	 * {@link #disconnect()} is called. If the verification is not enabled, nothing occurs.
	 * 
	 * @throws IOException If and I/O error occurs
	 */
	@Override
	public void update(int millis) throws IOException {
		if(!verifyConnection)
			return;
		if(millis - lastRead >= connectionTimeout){
			timeouts++;
			lastRead = millis;
			timeLastTimeout = millis;
		}
		if(timeouts >= maxTimeouts){
			disconnect();
		}
		if(timeouts > 0 && timeLastTimeout != -1 && 
				millis - timeLastTimeout > (connectionTimeout*3)){
			timeouts = 0;
			timeLastTimeout = -1;
		}
		if(millis - lastSend >= connectionTimeout / 2){
			writeHandshake();
		}
	}
	
	/**
	 * Updates the read verifiers of new data that has been received. Should be called when new data was read.
	 */
	protected void newDataRead(){
		lastRead = FlashUtil.millisInt();
	}
	/**
	 * Updates the send verifiers of new data that has been sent. Should be called when new data was sent.
	 */
	protected void newDataSent(){
		lastSend = FlashUtil.millisInt();
	}
	/**
	 * Resets the connection verification data handlers. Should be called on connection.
	 */
	protected void resetData(){
		lastSend = lastRead = FlashUtil.millisInt();
		timeLastTimeout = -1;
		timeouts = 0;
	}
	
	/**
	 * Sets whether or not to enable manual verification of connection. If set to true, data packets will be sent once
	 * in a while to verify connection still exists.
	 * @param enable true to enable, false to disable
	 */
	public void enableConnectionVerification(boolean enable){
		this.verifyConnection = enable;
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
	
	
	/**
	 * Attempts a manual server connection using {@link #HANDSHAKE_CONNECT_SERVER} and {@link #HANDSHAKE_CONNECT_CLIENT}.
	 * Waits for data to be received first. If the data is {@link #HANDSHAKE_CONNECT_CLIENT} than {@link #HANDSHAKE_CONNECT_SERVER}
	 * is sent and than waits for another {@link #HANDSHAKE_CONNECT_CLIENT} to be received.
	 * 
	 * @param commInterface interface for the communications
	 * @return true if connection was successful, false otherwise
	 * 
	 * @throws IOException If and I/O error occurs
	 */
	public static boolean handshakeServer(CommInterface commInterface) throws IOException{
		commInterface.setReadTimeout(READ_TIMEOUT * 4);
		byte[] data = commInterface.read();
		if(!isHandshakeClient(data, data.length))
			return false;
		
		commInterface.write(HANDSHAKE_CONNECT_SERVER);
		data = commInterface.read();
		if(!isHandshakeClient(data, data.length))
			return false;
		return true;
	}
	/**
	 * Attempts a manual client connection using {@link #HANDSHAKE_CONNECT_SERVER} and {@link #HANDSHAKE_CONNECT_CLIENT}.
	 * Sends {@link #HANDSHAKE_CONNECT_CLIENT} and waits to receive data. If the data is {@link #HANDSHAKE_CONNECT_SERVER}
	 * than {@link #HANDSHAKE_CONNECT_CLIENT} is sent again.
	 * 
	 * @param commInterface interface for the communications
	 * @return true if connection was successful, false otherwise
	 * 
	 * @throws IOException If and I/O error occurs
	 */
	public static boolean handshakeClient(CommInterface commInterface) throws IOException {
		commInterface.setReadTimeout(READ_TIMEOUT);
		commInterface.write(HANDSHAKE_CONNECT_CLIENT);
		
		byte[] data = commInterface.read();
		if(!isHandshakeServer(data, data.length))
			return false;
		
		commInterface.write(HANDSHAKE_CONNECT_CLIENT);
		return true;
	}
	
	/**
	 * Checks if an array matches {@link #HANDSHAKE}
	 * 
	 * @param bytes a byte array
	 * @param length length of data to check
	 * @return true if the array is equal to {@link #HANDSHAKE}
	 */
	public static boolean isHandshake(byte[] bytes, int length){
		if(length != HANDSHAKE.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != HANDSHAKE[i])
				return false;
		}
		return true;
	}
	/**
	 * Checks if an array matches {@link #HANDSHAKE_CONNECT_SERVER}
	 * 
	 * @param bytes a byte array
	 * @param length length of data to check
	 * @return true if the array is equal to {@link #HANDSHAKE_CONNECT_SERVER}
	 */
	public static boolean isHandshakeServer(byte[] bytes, int length){
		if(length != HANDSHAKE_CONNECT_SERVER.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != HANDSHAKE_CONNECT_SERVER[i])
				return false;
		}
		return true;
	}
	/**
	 * Checks if an array matches {@link #HANDSHAKE_CONNECT_CLIENT}
	 * 
	 * @param bytes a byte array
	 * @param length length of data to check
	 * @return true if the array is equal to {@link #HANDSHAKE_CONNECT_CLIENT}
	 */
	public static boolean isHandshakeClient(byte[] bytes, int length){
		if(length != HANDSHAKE_CONNECT_CLIENT.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != HANDSHAKE_CONNECT_CLIENT[i])
				return false;
		}
		return true;
	}
}
