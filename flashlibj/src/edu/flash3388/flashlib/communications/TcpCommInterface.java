package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * {@link CommInterface} using the TCP/IP model communications. Extends {@link StreamCommInterface} because TCP is a
 * stream-based communications protocol, but does not used the checksum corruption detection implemented in {@link StreamCommInterface}.
 * This interface can act as a Server or client interface, but capable of communicating with one other socket. When server, as soon
 * as communication is established, there is no attempt to listen to other handshake attempts.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class TcpCommInterface extends StreamCommInterface implements IpCommInterface{
	
	public static final int RECONNECTION_DELAY = 5000;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private int portOut, localPort;
	private InetAddress outInet, localInet;
	
	private boolean closed = false, reset = false;
	
	private OutputStream out;
	private InputStream in;
	
	private int lastRead = -1, timeLastTimeout = -1, lastSend = -1;
	private int connectionTimeout = CONNECTION_TIMEOUT, timeouts = 0, maxTimeouts = 3;
	
	/**
	 * Creates a new TCP/IP {@link CommInterface}. This constructor initializes a client TCP interface at a local
	 * address which is received from {@link InetAddress#getLocalHost()}, and port. Address and port for a server are immediately
	 * saved but used only when {@link #connect(Packet)} is called.
	 * 
	 * @param remote {@link InetAddress} object for the server address
	 * @param localport local port for data receiving
	 * @param remoteport server port
	 * 
	 * @throws UnknownHostException if the local host name could not be resolved into an address.
	 * @throws IOException if the bind operation fails, or if the socket is already bound.
	 */
	public TcpCommInterface(InetAddress remote, int localport, int remoteport) throws UnknownHostException, IOException{
		this(InetAddress.getLocalHost(), remote, localport, remoteport);
	}
	/**
	 * Creates a new TCP/IP {@link CommInterface}. This constructor initializes a client TCP interface at a local
	 * address and port. Address and port for a server are immediately saved but used only when {@link #connect(Packet)} 
	 * is called.
	 * 
	 * @param remote {@link InetAddress} object for the server address
	 * @param localport local port for data receiving
	 * @param remoteport server port
	 * @param local {@link InetAddress} object for the local binding address
	 * 
	 * @throws IOException if the bind operation fails, or if the socket is already bound.
	 */
	public TcpCommInterface(InetAddress local, InetAddress remote, int localport, int remoteport) throws IOException{
		super(false, false);
		outInet = remote;
		portOut = remoteport;
		localPort = localport;
		localInet = local;
		
		try {
			createSocket();
		} catch (IOException e) {
			closeSocket();
			throw e;
		}
	}
	/**
	 * Creates a new TCP/IP {@link CommInterface}. This constructor initializes a server TCP interface at a local
	 * address which is received from {@link InetAddress#getLocalHost()}, and port. A {@link ServerSocket} is created 
	 * with a backlog of 1 connection to listen for connections when {@link #connect(Packet)} is called.
	 * 
	 * @param localPort local binding port for listening socket
	 * 
	 * @throws IOException if an I/O error occurs when opening the socket.
	 */
	public TcpCommInterface(int localPort) throws IOException{
		this(InetAddress.getLocalHost(), localPort);
	}
	/**
	 * Creates a new TCP/IP {@link CommInterface}. This constructor initializes a server TCP interface at a local
	 * address and port. A {@link ServerSocket} is created with a backlog of 1 connection to listen for connections 
	 * when {@link #connect(Packet)} is called.
	 * 
	 * @param localAddr local binding address for listening socket
	 * @param localPort local binding port for listening socket
	 * 
	 * @throws IOException if an I/O error occurs when opening the socket.
	 */
	public TcpCommInterface(InetAddress localAddr, int localPort) throws IOException{
		super(true, false);
		localInet = localAddr;
		this.localPort = localPort;
		serverSocket = new ServerSocket(localPort, 1, localAddr);
	}
	
	private void createSocket() throws IOException{
		socket = new Socket();
		socket.bind(new InetSocketAddress(localInet, localPort));
	}
	private void closeSocket() throws IOException{
		if(socket != null && !socket.isClosed()){
			socket.close();
			socket = null;
		}
	}
	
	private void writeHandshake(){
		write(HANDSHAKE);
	}
	
	@Override
	protected boolean handleData(Packet packet) {
		boolean ret = super.handleData(packet);
		if(ret && CommInterface.isHandshake(packet.data, packet.length))
			return false;
		return ret;
	}
	
	/**
	 * Does nothing
	 */
	@Override
	public void open() {
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Closes the sockets. If bound as server, than the listening socket is terminated as well.
	 * Once executed, this interface cannot be used again for communications.
	 * </p>
	 */
	@Override
	public void close(){
		try {
			if(isBoundAsServer()) 
				serverSocket.close();
			closeSocket();
		} catch (IOException e) {
		}
		closed = true;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If bound as server, than we wait for connection event to the listening socket using {@link ServerSocket#accept()}.
	 * If client, we initialize the socket and connect to the server's remote address and port.
	 * </p>
	 */
	@Override
	public void connect(Packet packet){
		try {
			if(reset){
				FlashUtil.getLog().log("Reseting");
				resetBuffers();
				
				closeSocket();
				if (!isBoundAsServer()){
					FlashUtil.delay(RECONNECTION_DELAY);
					createSocket();
				}
				
				reset = false;
			}
			
			if(isBoundAsServer()){
				socket = serverSocket.accept();
				outInet = socket.getInetAddress();
			}
			else
				socket.connect(new InetSocketAddress(outInet, portOut));
			
			out = socket.getOutputStream();
			in = socket.getInputStream();
			
			lastRead = FlashUtil.millisInt();
			lastSend = FlashUtil.millisInt();
			timeLastTimeout = -1;
			timeouts = 0;
		} catch (IOException e) {	
			FlashUtil.getLog().reportError(e.getMessage());
		}finally{
			reset = true;
		}
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Closes the communications socket, but can still be used for communications.
	 * </p>
	 */
	@Override
	public void disconnect() {
		try {
			closeSocket();
		} catch (IOException e) {
			FlashUtil.getLog().reportError(e.getMessage());
		}
		if(isBoundAsServer())
			outInet = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadTimeout(int millis) {
		try {
			if(socket != null)
				socket.setSoTimeout(millis);
		} catch (IOException e) {}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTimeout() {
		try {
			return socket != null? socket.getSoTimeout() : 0;
		} catch (IOException e) {}
		return -1;
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
		return (socket != null? socket.isConnected() && !socket.isClosed() : false);
	}
	
	/**
	 * {@inheritDoc}
	 * Checks for timeouts in connection. If such events occur than connection is declared as lost.
	 */
	@Override
	public void update(int millis){
		/*if(outInet != null && millis - lastRemoteReachCheck > CONNECTION_TIMEOUT){
			System.out.println("Checking network reachable");
			try {
				if (!outInet.isReachable(CONNECTION_TIMEOUT)){
					disconnect();
					System.out.println("Network not reachable");
				}
			} catch (IOException e) {
			}
			lastRemoteReachCheck = millis;
		}*/
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
		if(millis - lastSend >= connectionTimeout / 2)
			writeHandshake();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If an {@link IOException} occurs during writing, than connection is terminated by calling {@link #disconnect()}.
	 * </p>
	 */
	@Override
	protected void writeData(byte[] data) {
		try {
			out.write(data);
			lastSend = FlashUtil.millisInt();
		} catch (IOException e) {
			disconnect();
		}
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If an {@link IOException} occurs during reading, than connection is terminated by calling {@link #disconnect()}.
	 * </p>
	 */
	@Override
	protected int readData(byte[] buffer) {
		try {
			int len = in.read(buffer);
			if(len > 0)
				lastRead = FlashUtil.millisInt();
			return len;
		} catch (SocketTimeoutException e) {
			return 0;
		} catch (IOException e) {
			disconnect();
			return 0;
		} 
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
	public void setLocalAddress(InetAddress addr) {
		if (isConnected() || !isOpened()) return;
		
		disconnect();
		
		try {
			localInet = addr;
			if(isBoundAsServer())
				serverSocket = new ServerSocket(localPort, 1, addr);
			else 
				createSocket();
		} catch (IOException e) {
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
			localPort = port;
			if(isBoundAsServer())
				serverSocket = new ServerSocket(localPort, 1, localInet);
			else 
				createSocket();
		} catch (IOException e) {
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
}
