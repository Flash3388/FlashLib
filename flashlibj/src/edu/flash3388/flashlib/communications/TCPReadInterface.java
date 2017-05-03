package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TCPReadInterface implements ReadInterface{

	private static final byte SEPERATOR_START = (byte)'<';
	private static final byte SEPERATOR_END = (byte)'>';
	
	public static final int BUFFER_SIZE = 100;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private int portOut, localPort;
	private InetAddress outInet, localInet;
	
	private boolean closed = false, server, reset = false;
	private byte[] data = new byte[BUFFER_SIZE], leftoverData = new byte[0];
	
	private OutputStream out;
	private InputStream in;
	
	public TCPReadInterface(CommInfo info) throws IOException{
		outInet = InetAddress.getByName(info.hostname);
		portOut = info.remotePort;
		localPort = info.localPort;
		localInet = InetAddress.getLocalHost();
		createSocket();
		server = false;
	}
	public TCPReadInterface(InetAddress remote, int localport, int remoteport) throws UnknownHostException, IOException{
		this(InetAddress.getLocalHost(), remote, localport, remoteport);
	}
	public TCPReadInterface(InetAddress local, InetAddress remote, int localport, int remoteport) throws UnknownHostException, IOException{
		outInet = remote;
		portOut = remoteport;
		localPort = localport;
		localInet = local;
		createSocket();
		server = false;
	}
	public TCPReadInterface(int localPort) throws IOException{
		this(InetAddress.getLocalHost(), localPort);
	}
	public TCPReadInterface(InetAddress localAddr, int localPort) throws IOException{
		localInet = localAddr;
		this.localPort = localPort;
		serverSocket = new ServerSocket(localPort, 20, localAddr);
		server = true;
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
			if(server) 
				serverSocket.close();
			if(socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		closed = true;
	}
	@Override
	public void connect(Packet packet){

		try {
			if(reset){
				leftoverData = new byte[0];
				for (int i = 0; i < data.length; i++) 
					data[i] = 0;
				
				if(socket != null && !socket.isClosed())
					socket.close();
				if(!server)
					createSocket();
				reset = false;
			}
			
			if(server){
				socket = serverSocket.accept();
			}
			else{
				socket.connect(new InetSocketAddress(outInet, portOut));
			}
			
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
				e.printStackTrace();
			}
			socket = null;
		}
		if(!server){
			try {
				createSocket();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean read(Packet packet) {
		if(!isOpened()) return false;
		try {
			int start = -1;
			if(leftoverData.length > 0){
				int startI = indexOf(leftoverData, 0, leftoverData.length-1, SEPERATOR_START);
				int endI = indexOf(leftoverData, 0, leftoverData.length-1, SEPERATOR_END);
				if(endI < startI)
					leftoverData = Arrays.copyOfRange(leftoverData, endI + 1, leftoverData.length - 1);
				
				start = getPacket(leftoverData, packet, startI, endI);
				if(start >= 0){
					if(packet.length + start + 2 > leftoverData.length - 1)
						leftoverData = new byte[0];
					else 
						leftoverData = Arrays.copyOfRange(leftoverData, packet.length + start + 2, leftoverData.length - 1);
					return true;
				}
			}
			
			int len = in.read(data);
			if(len < 1){
				packet.length = 0;
				return false;
			}
			
			leftoverData = Arrays.copyOf(leftoverData, leftoverData.length + len);
			System.arraycopy(data, 0, leftoverData, leftoverData.length - len, len);
			
			start = getPacket(leftoverData, packet);
			if(start >= 0){
				if(packet.length + start + 2 > leftoverData.length - 1)
					leftoverData = new byte[0];
				else 
					leftoverData = Arrays.copyOfRange(leftoverData, packet.length + start + 2, leftoverData.length - 1);
				return true;
			}
			return false;
		} catch (IOException e) {
			packet.length = 0;
			return false;
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
	public void write(byte[] data) {
		write(data, 0, data.length);
	}
	@Override
	public void write(byte[] data, int start, int length) {
		if(!isOpened()) return;
		try {
			byte[] sendData = new byte[length + 2];
			sendData[0] = SEPERATOR_START;
			sendData[sendData.length-1] = SEPERATOR_END;
			System.arraycopy(data, start, sendData, 1, length);
			out.write(sendData);
		} catch (IOException e) {}
	}
	
	@Override
	public boolean isOpened() {
		return !closed;
	}
	@Override
	public void setMaxBufferSize(int bytes) {
		data = new byte[bytes];
	}
	@Override
	public int getMaxBufferSize() {
		return data.length;
	}
	
	public boolean boundAsServer(){
		return server;
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
	
	public void update(long millis){}
	
	private static int indexOf(byte[] data, int start, int end, byte ch){
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
	}
	private static int getPacket(byte[] data, Packet packet, int start, int end){
		start = start < 0 ? indexOf(data, 0, data.length-1, SEPERATOR_START) : start;
		end = end < 0 ? indexOf(data, 0, data.length-1, SEPERATOR_END) : end;
		if(end < start || start < 0 || start == end){ 
			return -1;
		}
		
		byte[] p = new byte[end - start - 1];
		System.arraycopy(data, start + 1, p, 0, p.length);
		packet.data = p;
		packet.length = p.length;
		return start;
	}
	private static int getPacket(byte[] data, Packet packet){
		return getPacket(data, packet, -1, -1);
	}
}
