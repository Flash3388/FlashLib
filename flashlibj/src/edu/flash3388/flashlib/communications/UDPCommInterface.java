package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * {@link CommInterface} using the UDP model communications. Uses a {@link DatagramSocket} for communications. Client-Server
 * relationship is used to determine who initiates communications. To insure connection, this comm interface extends the abstract
 * {@link ManualConnectionVerifier}.
 * <p>
 * Connection is insured by checking the traffic going through the port. If data was not sent in a while, an handshake will
 * be sent instead. If data was not received for a while a timeout will occur. Once a defined amount of timeouts have occurred,
 * the connection will be considered lost.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class UDPCommInterface extends ManualConnectionVerifier implements IPCommInterface{
	
	private DatagramSocket socket;
	private int portOut = -1;
	private InetAddress outInet;
	private int localPort;
	private InetAddress localInet;
	private int timeout;
	
	private boolean closed = false;
	private byte[] data = new byte[BUFFER_SIZE];
	private boolean server = false, replace = false, isConnected = false;
	
	/**
	 * Constructs a client-type UDP interface. A {@link DatagramSocket} is created and bound to a provided port and
	 * a {@link InetAddress#isAnyLocalAddress() Wildcard} address provided by the kernel. The remote data is saved and
	 * used only when {@link #connect()} is called.
	 * 
	 * @param remote remote server address
	 * @param localport local port to use
	 * @param remoteport remote server port
	 */
	public UDPCommInterface(InetAddress remote, int localport, int remoteport) {
		outInet = remote;
		this.localPort = localport;
		portOut = remoteport;
		server = false;
	}
	/**
	 * Constructs a server-type UDP interface. A {@link DatagramSocket} is created and bound to a provided port and
	 * a {@link InetAddress#isAnyLocalAddress() Wildcard} address provided by the kernel.
	 * 
	 * @param localPort local port to use
	 */
	public UDPCommInterface(int localPort) {
		this(null, localPort);
	}
	/**
	 * Constructs a server-type UDP interface. A {@link DatagramSocket} is created and bound to a provided port and
	 * address. 
	 * 
	 * @param localAddr local bind address
	 * @param localPort local port to use
	 */
	public UDPCommInterface(InetAddress localAddr, int localPort) {
		localInet = localAddr;
		this.localPort = localPort;
		server = true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open() throws IOException {
		socket = new DatagramSocket(localPort, localInet);
		socket.setSoTimeout(timeout);
	}
	/**
	 * {@inheritDoc}
	 * Closes the socket. This interface cannot be used after that.
	 */
	@Override
	public void close() throws IOException {
		socket.close();
		closed = true;
	}
	
	/**
	 * {@inheritDoc}
	 * Execute an handshake based on the type of the connection: server or client.
	 */
	@Override
	public void connect() throws IOException {
		allowReplacingOfRemote(true);
		isConnected = server? handshakeServer(this) : 
			handshakeClient(this);
		allowReplacingOfRemote(false);
		resetData();
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
	 * <p>
	 * If the connection type is a server, or replacing of remote data was defined, and data from a new source was 
	 * received than that source is now the remote connection.
	 * </p>
	 */
	@Override
	public byte[] read() throws IOException {
		DatagramPacket recp = new DatagramPacket(data, data.length);
		socket.receive(recp);
		
		if(server && ((portOut < 0 && outInet == null) || replace)){
			outInet = recp.getAddress();
			portOut = recp.getPort();
		}else if(recp.getPort() != portOut || 
				!FlashUtil.equals(outInet.getAddress(), recp.getAddress().getAddress())){
			FlashUtil.getLogger().warning(String.format("Unknown sender to socket: %s:%s",
					recp.getAddress().getHostAddress(), 
					recp.getPort()));
		}
		
		newDataRead();
		byte[] data = recp.getData();
		int length = recp.getLength();
		
		if(isHandshake(data, length)){
			return length != data.length? Arrays.copyOf(data, length) :
				data;
		}
		
		return length != data.length? Arrays.copyOf(data, length) :
			data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadTimeout(int millis) throws IOException {
		this.timeout = millis;
		if (isOpened())
			socket.setSoTimeout(millis);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getReadTimeout() throws IOException {
		return isOpened() ? socket.getSoTimeout() : timeout;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data) throws IOException {
		write(data, 0, data.length);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data, int start, int length) throws IOException {
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
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	public void write(byte[] data, int start, int length, InetAddress outInet, int portOut) throws IOException {
		newDataSent();
		socket.send(new DatagramPacket(data, start, length, outInet, portOut));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxBufferSize(int bytes) throws IOException {
		data = new byte[bytes];
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxBufferSize() throws IOException {
		return data.length;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isServer(){
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
		return localInet;
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
		return localPort;
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
	public void setLocalAddress(InetAddress addr) throws IOException {
		if (isConnected())
			throw new IllegalStateException("Cannot change local address while connected");
		
		localInet = addr;
		
		if (isOpened()) {
			close();
			open();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRemoteAddress(InetAddress addr) throws IOException {
		if (isConnected()) 
			throw new IllegalStateException("Cannot change remote address while connected");
		if (isServer())
			throw new IllegalStateException("Cannot change remote address for server interface");
		
		outInet = addr;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocalPort(int port) throws IOException {
		if (isConnected()) 
			throw new IllegalStateException("Cannot change local port while connected");
		
		localPort = port;
		
		if (isOpened()) {
			close();
			open();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRemotePort(int port) throws IOException {
		if (isConnected()) 
			throw new IllegalStateException("Cannot change remote port while connected");
		if (isServer())
			throw new IllegalStateException("Cannot change remote port for server interface");
		
		portOut = port;
	}
}
