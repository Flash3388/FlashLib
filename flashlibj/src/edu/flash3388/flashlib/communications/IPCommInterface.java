package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.net.InetAddress;

public interface IPCommInterface extends CommInterface{
	
	/**
	 * Gets whether or not this interface is a server:
	 * 
	 * @return true - server, false - client
	 */
	boolean isServer();
	
	/**
	 * Gets the local bound address
	 * @return address for local bind
	 */
	InetAddress getLocalAddress();
	/**
	 * Gets the remote side address.
	 * @return address of remote size
	 */
	InetAddress getRemoteAddress();
	/**
	 * Gets the local port for socket binding.
	 * @return port for local data listening
	 */
	int getLocalPort();
	/**
	 * Gets the remote data port.
	 * @return remote data port
	 */
	int getRemotePort();
	
	/**
	 * Sets the local bin address.
	 * @param addr address for local binding
	 * 
	 * @throws IOException if an IO error occurs
	 */
	void setLocalAddress(InetAddress addr) throws IOException;
	/**
	 * Sets the remote side address.
	 * @param addr address of remote size
	 * 
	 * @throws IOException if an IO error occurs
	 */
	void setRemoteAddress(InetAddress addr) throws IOException;
	/**
	 * Sets the local port for socket binding.
	 * @param port port for local data listening
	 * 
	 * @throws IOException IOException if an IO error occurs
	 */
	void setLocalPort(int port) throws IOException;
	/**
	 * Sets the remote port for socket connecting.
	 * @param port port for remote data connecting
	 * 
	 * @throws IOException IOException if an IO error occurs
	 */
	void setRemotePort(int port) throws IOException;
}
