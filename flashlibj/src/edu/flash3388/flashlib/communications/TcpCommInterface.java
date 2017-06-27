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
public class TcpCommInterface extends StreamCommInterface{
	
	private ServerSocket serverSocket;
	private Socket socket;
	private int portOut, localPort;
	private InetAddress outInet, localInet;
	
	private boolean closed = false, reset = false;
	
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
		createSocket();
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
		FlashUtil.getLog().log("Trying to create socket");
		socket = new Socket();
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(localInet, localPort));
		FlashUtil.getLog().log("Created socket");
	}
	
	/**
	 * Does nothing
	 */
	@Override
	public void open() {
	}
	/**
	 * Closes the sockets. If bound as server, than the listening socket is terminated as well.
	 * Once executed, this interface cannot be used again for communications.
	 */
	@Override
	public void close(){
		try {
			if(isBoundAsServer()) 
				serverSocket.close();
			if(socket != null)
				socket.close();
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
				
				if(socket != null && (!socket.isClosed() || !socket.isBound()))
					socket.close();
				if (!isBoundAsServer() && (socket == null || socket.isClosed() || !socket.isBound()))
					createSocket();
				
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
		} catch (IOException e) {	
			e.printStackTrace();
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
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
			}
			socket = null;
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
				socket.setSoTimeout((int)millis);
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
		try {
			return (socket != null? socket.isConnected() : false) &&
					(outInet != null && outInet.isReachable(1000));
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Gets the local port for socket binding.
	 * @return port for local data listening
	 */
	public int getLocalPort(){
		return localPort;
	}
	/**
	 * Gets the remote data port.
	 * @return remote data port
	 */
	public int getRemotePort(){
		return portOut;
	}
	/**
	 * Gets the remote side address.
	 * @return address of remote size
	 */
	public InetAddress getRemoteAddress(){
		return outInet;
	}
	/**
	 * Gets the local bound address
	 * @return address for local bind
	 */
	public InetAddress getLocalAddress(){
		return localInet;
	}
	
	/**
	 * Does nothing
	 */
	@Override
	public void update(int millis){}
	
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
		} catch (IOException e) {
			disconnect();
			FlashUtil.getLog().log("Write Exception");
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
			return in.read(buffer);
		} catch (SocketTimeoutException e) {
			return 0;
		} catch (IOException e) {
			disconnect();
			FlashUtil.getLog().log("Read Exception");
			return 0;
		} 
	}
}
