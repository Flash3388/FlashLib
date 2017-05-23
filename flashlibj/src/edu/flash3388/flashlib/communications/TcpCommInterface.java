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

public class TcpCommInterface extends StreamCommInterface{
	
	private ServerSocket serverSocket;
	private Socket socket;
	private int portOut, localPort;
	private InetAddress outInet, localInet;
	
	private boolean closed = false, reset = false;
	
	private OutputStream out;
	private InputStream in;
	
	public TcpCommInterface(InetAddress remote, int localport, int remoteport) throws UnknownHostException, IOException{
		this(InetAddress.getLocalHost(), remote, localport, remoteport);
	}
	public TcpCommInterface(InetAddress local, InetAddress remote, int localport, int remoteport) throws UnknownHostException, IOException{
		super(false, false);
		outInet = remote;
		portOut = remoteport;
		localPort = localport;
		localInet = local;
		createSocket();
	}
	public TcpCommInterface(int localPort) throws IOException{
		this(InetAddress.getLocalHost(), localPort);
	}
	public TcpCommInterface(InetAddress localAddr, int localPort) throws IOException{
		super(true, false);
		localInet = localAddr;
		this.localPort = localPort;
		serverSocket = new ServerSocket(localPort, 20, localAddr);
	}
	
	private void createSocket() throws IOException{
		socket = new Socket();
		socket.bind(new InetSocketAddress(localInet, localPort));
	}
	
	@Override
	public void open() {
	}
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
	@Override
	public void connect(Packet packet){
		try {
			if(reset){
				resetBuffers();
				
				if(socket != null && !socket.isClosed())
					socket.close();
				if (!isBoundAsServer())
					createSocket();
				
				reset = false;
			}
			
			if(isBoundAsServer())
				socket = serverSocket.accept();
			else
				socket.connect(new InetSocketAddress(outInet, portOut));
			
			out = socket.getOutputStream();
			in = socket.getInputStream();
			reset = true;
		} catch (IOException e) {	
		}
	}
	@Override
	public void disconnect() {
		if(!isConnected()) return;
		
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
			}
			socket = null;
		}
	}
	
	@Override
	public void setReadTimeout(long millis) {
		try {
			if(socket != null)
				socket.setSoTimeout((int)millis);
		} catch (IOException e) {}
	}
	@Override
	public long getTimeout() {
		try {
			return socket != null? socket.getSoTimeout() : 0;
		} catch (IOException e) {}
		return -1;
	}
	
	@Override
	public boolean isOpened() {
		return !closed;
	}
	@Override
	public boolean isConnected() {
		return socket != null? socket.isConnected() : false;
	}
	
	public int getLocalPort(){
		return localPort;
	}
	public int getRemotePort(){
		return portOut;
	}
	public InetAddress getRemoteAddress(){
		return outInet;
	}
	
	@Override
	public void update(long millis){}
	
	@Override
	protected void writeData(byte[] data) {
		try {
			out.write(data);
		} catch (IOException e) {
			disconnect();
		}
	}
	@Override
	protected int readData(byte[] buffer) {
		try {
			return in.read(buffer);
		} catch (SocketTimeoutException e) {
			return 0;
		} catch (IOException e) {
			disconnect();
			return 0;
		} 
	}
}
