package edu.flash3388.flashlib.communications;

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
	 */
	void setLocalAddress(InetAddress addr);
	/**
	 * Sets the remote side address.
	 * @param addr address of remote size
	 */
	void setRemoteAddress(InetAddress addr);
	/**
	 * Sets the local port for socket binding.
	 * @param port port for local data listening
	 */
	void setLocalPort(int port);
	/**
	 * Sets the remote port for socket connecting.
	 * @param port port for remote data connecting
	 */
	void setRemotePort(int port);
}
