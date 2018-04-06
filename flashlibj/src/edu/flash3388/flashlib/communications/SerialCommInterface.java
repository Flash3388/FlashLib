package edu.flash3388.flashlib.communications;

import java.io.IOException;

/**
 * An abstract logic for IO port {@link CommInterface}s. Extends {@link StreamCommInterface} and adds manual connection
 * tracking to insure the existence of connection. Does not provide any data recovery. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class SerialCommInterface extends StreamCommInterface {
	
	private boolean isConnected = false;
	private boolean server;
	
	/**
	 * Creates a new CommInterface for IO ports. Data is passed to the super class {@link StreamCommInterface}.
	 * 
	 * @param server indicates whether this interface initiates connection - client or waits for connection - server
	 * @param crcallow if true, data will be processed with CRC32 to dump data corruption
	 */
	public SerialCommInterface(boolean server, boolean crcallow){
		super(crcallow);
		this.server = server;
	}
	/**
	 * Creates a new CommInterface for IO ports. Data is passed to the super class {@link StreamCommInterface}.
	 * CRC32 is initialized through this constructor. To control CRC usage, use {@link #SerialCommInterface(boolean, boolean)}.
	 * 
	 * @param server indicates whether this interface initiates connection - client or waits for connection - server
	 */
	public SerialCommInterface(boolean server){
		super(server);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect() throws IOException {
		isConnected = isServer()? handshakeServer(this) : 
			handshakeClient(this);
		if(isConnected){
			resetData();
			resetBuffers();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect() throws IOException {
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
	 * Gets whether or not this interface acts like as a server:
	 * <ul>
	 * 	<li> Server: waits for connection </li>
	 * 	<li> Client: initiates connection </li>
	 * </ul>
	 * 
	 * @return true - server, false - client
	 */
	public boolean isServer(){
		return server;
	}
}
