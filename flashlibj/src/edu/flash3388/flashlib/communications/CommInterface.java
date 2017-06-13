package edu.flash3388.flashlib.communications;

/**
 * Provides an IO port interface for communication. Used by {@link Communications}, this interface allows the communication
 * system to manage communication without needing to care for the way the data is sent or received. It allows the system
 * to work with any communication method imaginable.
 * <p>
 * For example:
 * <ul>
 * 		<li> IP sockets: TCP, UDP</li>
 * 		<li> Data buses: I2C, SPI, CAN </li>
 * 		<li> Data ports: Serial, Parallel </li>
 * 		<li> A combination of methods, like: UDP with data recovery, etc <li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface CommInterface {
	/**
	 * A default data buffer size for reading buffers in bytes
	 */
	public static final int BUFFER_SIZE = 100;
	/**
	 * A default timeout for reading blocking call in milliseconds
	 */
	public static final int READ_TIMEOUT = 20;
	
	/**
	 * An handshake byte array. Used by {@link UdpCommInterface} and {@link PortCommInterface}
	 */
	public static final byte[] HANDSHAKE = {0x01, 0xe, 0x07};
	/**
	 * A server connection handshake. Used by {@link UdpCommInterface} and {@link PortCommInterface}
	 */
	public static final byte[] HANDSHAKE_CONNECT_SERVER = {0xb, 0x02, 0xa};
	/**
	 * A client connection handshake. Used by {@link UdpCommInterface} and {@link PortCommInterface}
	 */
	public static final byte[] HANDSHAKE_CONNECT_CLIENT = {0xc, 0x10, 0x06};
	
	/**
	 * A default connection timeout for protocols which need to manually check for connection, like: UDP.
	 */
	public static final int CONNECTION_TIMEOUT = 1500;
	
	/**
	 * Opens the communications port for usage. Must be done before the port can be used.
	 */
	void open();
	/**
	 * Closes the communications port. Once done, the port can no longer be used for communications.
	 */
	void close();
	/**
	 * Gets whether this port is open and ready for usage.
	 * @return true if open, false otherwise.
	 */
	boolean isOpened();
	
	/**
	 * Initiates connection with a remote side. 
	 * 
	 * @param packet {@link Packet} object for data receiving
	 */
	void connect(Packet packet);
	/**
	 * Closes connection with a remote side.
	 */
	void disconnect();
	/**
	 * Gets whether this port is connected to a remote communications port.
	 * @return true if connected, false otherwise.
	 */
	boolean isConnected();
	
	/**
	 * Reads data from the port and stores the result in a {@link Packet} object. If the port is closed or not connected
	 * nothing will happen. 
	 * 
	 * @param packet the packet for storing read data
	 * @return true if data was read, false otherwise.
	 */
	boolean read(Packet packet);
	/**
	 * Writes data to the communications port to be sent. If the port is closed or not connected nothing will happen. 
	 * 
	 * @param data array of bytes to be sent
	 */
	void write(byte[] data);
	/**
	 * Writes data to the communications port to be sent. If the port is closed or not connected nothing will happen. 
	 * 
	 * @param data array of bytes to be sent
	 * @param start start index of data in the array
	 * @param length amount of data in the array
	 */
	void write(byte[] data, int start, int length);
	
	/**
	 * Sets timeout for the reading blocking call in milliseconds.
	 * 
	 * @param millis timeout for reading call milliseconds
	 */
	void setReadTimeout(int millis);
	/**
	 * Gets the timeout for the reading blocking call in milliseconds.
	 * @return timeout for reading call in milliseconds.
	 */
	int getTimeout();
	
	/**
	 * Sets the maximum size of the reading data buffer. This is the maximum amount of bytes that can
	 * be read from the port.
	 * 
	 * @param bytes maximum amount of bytes in the data buffer.
	 */
	void setMaxBufferSize(int bytes);
	/**
	 * Gets the maximum size of the reading data buffer. This is the maximum amount of bytes that can
	 * be read from the port.
	 * @return maximum amount of bytes in the data buffer.
	 */
	int getMaxBufferSize();

	/**
	 * Allows for updating data tracking and protocols. Called by {@link Communications} during every iteration of its
	 * communications thread.
	 * 
	 * @param millis the current time in milliseconds
	 */
	void update(int millis);
	
	
	/**
	 * Attempts a manual server connection using {@link #HANDSHAKE_CONNECT_SERVER} and {@link #HANDSHAKE_CONNECT_CLIENT}.
	 * Waits for data to be received first. If the data is {@link #HANDSHAKE_CONNECT_CLIENT} than {@link #HANDSHAKE_CONNECT_SERVER}
	 * is sent and than waits for another {@link #HANDSHAKE_CONNECT_CLIENT} to be received.
	 * 
	 * @param commInterface interface for the communications
	 * @param packet packet for data storage
	 * @return true if connection was successful, false otherwise
	 */
	public static boolean handshakeServer(CommInterface commInterface, Packet packet){
		commInterface.setReadTimeout(READ_TIMEOUT * 4);
		commInterface.read(packet);
		if(!isHandshakeClient(packet.data, packet.length))
			return false;
		
		commInterface.write(HANDSHAKE_CONNECT_SERVER);
		commInterface.read(packet);
		if(!isHandshakeClient(packet.data, packet.length))
			return false;
		return true;
	}
	/**
	 * Attempts a manual client connection using {@link #HANDSHAKE_CONNECT_SERVER} and {@link #HANDSHAKE_CONNECT_CLIENT}.
	 * Sends {@link #HANDSHAKE_CONNECT_CLIENT} and waits to receive data. If the data is {@link #HANDSHAKE_CONNECT_SERVER}
	 * than {@link #HANDSHAKE_CONNECT_CLIENT} is sent again.
	 * 
	 * @param commInterface interface for the communications
	 * @param packet packet for data storage
	 * @return true if connection was successful, false otherwise
	 */
	public static boolean handshakeClient(CommInterface commInterface, Packet packet){
		commInterface.setReadTimeout(READ_TIMEOUT);
		commInterface.write(HANDSHAKE_CONNECT_CLIENT);
		
		commInterface.read(packet);
		if(!isHandshakeServer(packet.data, packet.length))
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
