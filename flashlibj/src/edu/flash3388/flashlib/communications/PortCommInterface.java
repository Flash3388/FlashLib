package edu.flash3388.flashlib.communications;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An abstract logic for IO port {@link CommInterface}s. Extends {@link StreamCommInterface} and adds manual connection
 * tracking to insure the existence of connection. Does not provide any data recovery. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class PortCommInterface extends StreamCommInterface{
	
	private boolean isConnected = false;
	
	/**
	 * Creates a new CommInterface for IO ports. Data is passed to the super class {@link StreamCommInterface}.
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
	 * 
	 * @param server indicates whether this interface initiates connection - client or waits for connection - server
	 */
	public PortCommInterface(boolean server){
		super(server);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect(Packet packet) {
		isConnected = isBoundAsServer()? handshakeServer(this, packet) : 
			handshakeClient(this, packet);
		if(isConnected){
			resetData();
			resetBuffers();
		}
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
}
