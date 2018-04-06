package edu.flash3388.flashlib.communications;

import java.io.IOException;

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
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	void open() throws IOException;
	/**
	 * Closes the communications port. Once done, the port can no longer be used for communications.
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	void close() throws IOException;
	/**
	 * Gets whether this port is open and ready for usage.
	 * @return true if open, false otherwise.
	 */
	boolean isOpened();
	
	/**
	 * Initiates connection with the remote side. 
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	void connect() throws IOException;
	/**
	 * Closes connection with the remote side.
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	void disconnect() throws IOException;
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
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	byte[] read() throws IOException;
	/**
	 * Writes data to the communications port to be sent. If the port is closed or not connected nothing will happen. 
	 * <p>
	 * Default implementation calls {@link #write(byte[], int, int)}, passing it
	 * the array, 0, and the array length.
	 * 
	 * @param data array of bytes to be sent
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	default void write(byte[] data) throws IOException {
		write(data, 0, data.length);
	}
	/**
	 * Writes data to the communications port to be sent. If the port is closed or not connected nothing will happen. 
	 * 
	 * @param data array of bytes to be sent
	 * @param start start index of data in the array
	 * @param length amount of data in the array
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	void write(byte[] data, int start, int length) throws IOException;
	
	/**
	 * Sets timeout for the reading blocking call in milliseconds.
	 * 
	 * @param millis timeout for reading call milliseconds
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	void setReadTimeout(int millis) throws IOException;
	/**
	 * Gets the timeout for the reading blocking call in milliseconds.
	 * @return timeout for reading call in milliseconds.
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	int getReadTimeout() throws IOException;
	
	/**
	 * Sets the maximum size of the reading data buffer. This is the maximum amount of bytes that can
	 * be read from the port.
	 * 
	 * @param bytes maximum amount of bytes in the data buffer.
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	void setMaxBufferSize(int bytes) throws IOException;
	/**
	 * Gets the maximum size of the reading data buffer. This is the maximum amount of bytes that can
	 * be read from the port.
	 * @return maximum amount of bytes in the data buffer.
	 * 
	 * @throws IOException If an IO error occurs
	 */
	int getMaxBufferSize() throws IOException;

	/**
	 * Allows for updating data tracking and protocols. Called by {@link Communications} during every iteration of its
	 * communications thread.
	 * 
	 * @param millis the current time in milliseconds
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	void update(int millis) throws IOException;
}
