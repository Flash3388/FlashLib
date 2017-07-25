package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * {@link CommInterface} using the UDP model communications. Uses a {@link DatagramSocket} for communications. Client-Server
 * relationship is used to determine who initiates communications. To insure connection, this comm interface extends the abstract
 * {@link ManualConnectionVerifierInerface}.
 * <p>
 * Connection is insured by checking the traffic going through the port. If data was not sent in a while, an handshake will
 * be sent instead. If data was not received for a while a timeout will occur. Once a defined amount of timeouts have occurred,
 * the connection will be considered lost.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class UdpCommInterface extends ManualConnectionVerifierInerface implements IpCommInterface{
	
	private DatagramSocket socket;
	private int portOut = -1;
	private InetAddress outInet;
	
	private boolean closed = false;
	private byte[] data = new byte[BUFFER_SIZE];
	private boolean server = false, replace = false, isConnected = false;
	
	/**
	 * Constructs a client-type UDP interface. A {@link DatagramSocket} is created and bound to a provided port and
	 * a {@link InetAddress#isAnyLocalAddress() Wildcard} address provided by the kernel. The remote data is saved and
	 * used only when {@link #connect(Packet)} is called.
	 * 
	 * @param remote remote server address
	 * @param localport local port to use
	 * @param remoteport remote server port
	 * 
	 * @throws SocketException if the socket could not be opened, or the socket could not bind to the specified local port.
	 */
	public UdpCommInterface(InetAddress remote, int localport, int remoteport) throws SocketException{
		outInet = remote;
		socket = new DatagramSocket(localport);
		portOut = remoteport;
		server = false;
	}
	/**
	 * Constructs a server-type UDP interface. A {@link DatagramSocket} is created and bound to a provided port and
	 * a {@link InetAddress#isAnyLocalAddress() Wildcard} address provided by the kernel.
	 * 
	 * @param localPort local port to use
	 * 
	 * @throws SocketException if the socket could not be opened, or the socket could not bind to the specified local port.
	 */
	public UdpCommInterface(int localPort) throws SocketException{
		this(null, localPort);
	}
	/**
	 * Constructs a server-type UDP interface. A {@link DatagramSocket} is created and bound to a provided port and
	 * address. 
	 * 
	 * @param localAddr local bind address
	 * @param localPort local port to use
	 * 
	 * @throws SocketException if the socket could not be opened, or the socket could not bind to the specified local port.
	 */
	public UdpCommInterface(InetAddress localAddr, int localPort) throws SocketException{
		socket = new DatagramSocket(localPort, localAddr);
		server = true;
	}
	
	/**
	 * Does nothing
	 */
	@Override
	public void open() {
	}
	/**
	 * {@inheritDoc}
	 * Closes the socket. This interface cannot be used after that.
	 */
	@Override
	public void close() {
		socket.close();
		closed = true;
	}
	
	/**
	 * {@inheritDoc}
	 * Execute an handshake based on the type of the connection: server or client.
	 */
	@Override
	public void connect(Packet packet) {
		allowReplacingOfRemote(true);
		isConnected = server? handshakeServer(this, packet) : 
			handshakeClient(this, packet);
		allowReplacingOfRemote(false);
		resetData();
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
	 * <p>
	 * If the connection type is a server, or replacing of remote data was defined, and data from a new source was 
	 * received than that source is now the remote connection.
	 * </p>
	 */
	@Override
	public boolean read(Packet packet) {
		if(!isOpened()) return false;
		try {
			DatagramPacket recp = new DatagramPacket(data, data.length);
			socket.receive(recp);
			
			if(server && ((portOut < 0 && outInet == null) || replace)){
				outInet = recp.getAddress();
				portOut = recp.getPort();
			}else if(recp.getPort() != portOut || 
					!FlashUtil.equals(outInet.getAddress(), recp.getAddress().getAddress())){
				FlashUtil.getLog().log("Unknow sender!!");
			}
			
			newDataRead();
			packet.senderAddress = outInet;
			packet.senderPort = portOut;
			packet.data = recp.getData();
			packet.length = recp.getLength();
			
			if(isHandshake(packet.data, packet.length)){
				packet.length = 1;
				return true;
			}
			/*if(server && isHandshakeClient(packet.data, packet.length)){
				write(HANDSHAKE_CONNECT_SERVER);
				packet.length = 1;
				return true;
			}
			if(!server && isHandshakeServer(packet.data, packet.length)){
				write(HANDSHAKE_CONNECT_CLIENT);
				packet.length = 1;
				return true;
			}*/

			
			return true;
		} catch (IOException e) {
			packet.length = 0;
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadTimeout(int millis) {
		try {
			socket.setSoTimeout(millis);
		} catch (SocketException e) {}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTimeout() {
		try {
			return socket.getSoTimeout();
		} catch (SocketException e) {}
		return -1;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data) {
		write(data, 0, data.length);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data, int start, int length) {
		if(!isOpened()) return;
		write(data, start, length, outInet, portOut);
	}
	
	/**
	 * Writes data to a specific address and port instead of the saved address and port.
	 * 
	 * @param data byte array to send
	 * @param start data offset in the byte array
	 * @param length amount of bytes to send
	 * @param outInet remote address
	 * @param portOut remote port
	 */
	public void write(byte[] data, int start, int length, InetAddress outInet, int portOut){
		if(!isOpened()) return;
		try {
			newDataSent();
			socket.send(new DatagramPacket(data, start, length, outInet, portOut));
		} catch (IOException e) {}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxBufferSize(int bytes) {
		data = new byte[bytes];
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxBufferSize() {
		return data.length;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBoundAsServer(){
		return server;
	}
	
	
	/**
	 * Sets whether or not replacing of remote connection is allowed.
	 * If true, than new data from an unknown remote will make that remote as the new saved remote target. Otherwise the
	 * data is ignored.
	 * 
	 * @param re if true, foreign sender's data will not be filtered
	 */
	public void allowReplacingOfRemote(boolean re){
		replace = re;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOpened() {
		return !closed;
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
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public InetAddress getRemoteAddress() {
		return outInet;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLocalPort() {
		return socket.getLocalPort();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRemotePort() {
		return portOut;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocalAddress(InetAddress addr) {
		if (isConnected() || !isOpened()) return;
		
		disconnect();
		
		try {
			socket = new DatagramSocket(socket.getLocalPort(), addr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRemoteAddress(InetAddress addr) {
		if (isConnected() || !isOpened() || isBoundAsServer()) return;
		outInet = addr;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocalPort(int port) {
		if (isConnected() || !isOpened()) return;
		
		disconnect();
		
		try {
			socket = new DatagramSocket(port, socket.getLocalAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRemotePort(int port) {
		if (isConnected() || !isOpened() || isBoundAsServer()) return;
		portOut = port;
	}
}
