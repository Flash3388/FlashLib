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
public class TCPCommInterface extends StreamCommInterface implements IPCommInterface{
	
	public static final int RECONNECTION_DELAY = 5000;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private int portOut, localPort;
	private InetAddress outInet, localInet;
	
	private boolean closed = false, reset = false;
	private int readTimeout = 0;
	
	private OutputStream out;
	private InputStream in;
	
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
	public TCPCommInterface(InetAddress remote, int localport, int remoteport) throws UnknownHostException, IOException{
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
	public TCPCommInterface(InetAddress local, InetAddress remote, int localport, int remoteport) throws IOException{
		super(false);
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
	public TCPCommInterface(int localPort) throws IOException{
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
	public TCPCommInterface(InetAddress localAddr, int localPort) throws IOException{
		super(false);
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
			if(isServer()) 
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
				closeSocket();
				if (!isServer()){
					FlashUtil.delay(RECONNECTION_DELAY);
					createSocket();
				}
				
				reset = false;
			}
			
			if(isServer()){
				socket = serverSocket.accept();
				outInet = socket.getInetAddress();
			}
			else
				socket.connect(new InetSocketAddress(outInet, portOut));
			
			out = socket.getOutputStream();
			in = socket.getInputStream();
			
			setReadTimeout(readTimeout);
			resetBuffers();
			resetData();
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
		if(isServer())
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
			readTimeout = millis;
		} catch (IOException e) {}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTimeout() {
		try {
			return socket != null? socket.getSoTimeout() : readTimeout;
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
	 * <p>
	 * If an {@link IOException} occurs during writing, than connection is terminated by calling {@link #disconnect()}.
	 * </p>
	 */
	@Override
	protected void writeRaw(byte[] data, int start, int length) {
		try {
			out.write(data, start, length);
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
	protected int readRaw(byte[] buffer, int start, int length) {
		try {
			return in.read(buffer, start, length);
		} catch (SocketTimeoutException e) {
			return 0;
		} catch (IOException e) {
			disconnect();
			return 0;
		} 
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the amount of bytes in the TCP buffer.
	 */
	@Override
	protected int availableData() {
		try {
			return in.available();
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
			if(isServer())
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
		if (isConnected() || !isOpened() || isServer()) return;
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
			if(isServer())
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
		if (isConnected() || !isOpened() || isServer()) return;
		portOut = port;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isServer() {
		return serverSocket != null;
	}
}
