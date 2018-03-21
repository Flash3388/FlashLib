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
	 */
	void connect();
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
	 * Reads data from the port and returns the data read. If the port is closed or not connected
	 * nothing will happen. 
	 * 
	 * @return byte[] array of bytes read, or null if no data was read.
	 */
	byte[] read();
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
}
