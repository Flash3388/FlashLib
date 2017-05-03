package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import edu.flash3388.flashlib.util.FlashUtil;

public class UDPReadInterface implements ReadInterface{

	public static final byte[] HANDSHAKE = {0x01, 0xe, 0x07};
	public static final byte[] HANDSHAKE_CONNECT_SERVER = {0xb, 0x02, 0xa};
	public static final byte[] HANDSHAKE_CONNECT_CLIENT = {0xc, 0x10, 0x06};
	
	public static final int CONNECTION_TIMEOUT = 1500;
	public static final int READ_TIMEOUT = 20;
	public static final int BUFFER_SIZE = 100;
	
	private DatagramSocket socket;
	private int portOut = -1;
	private InetAddress outInet;
	
	private boolean closed = false;
	private byte[] data = new byte[BUFFER_SIZE];
	private boolean server = false, replace = false, isConnected = false;
	private long lastRead = -1, timeLastTimeout = -1;
	private int connectionTimeout = CONNECTION_TIMEOUT, timeouts = 0, maxTimeouts = 3;
	
	public UDPReadInterface(CommInfo info) throws SocketException, UnknownHostException{
		outInet = InetAddress.getByName(info.hostname);
		socket = new DatagramSocket(info.localPort);
		portOut = info.remotePort;
		server = false;
	}
	public UDPReadInterface(InetAddress remote, int localport, int remoteport) throws SocketException{
		outInet = remote;
		socket = new DatagramSocket(localport);
		portOut = remoteport;
		server = false;
	}
	public UDPReadInterface(int localPort) throws SocketException{
		this(null, localPort);
	}
	public UDPReadInterface(InetAddress localAddr, int localPort) throws SocketException{
		socket = new DatagramSocket(localPort, localAddr);
		server = true;
	}
	
	@Override
	public void open() {
	}
	@Override
	public void close() {
		socket.close();
		closed = true;
	}
	@Override
	public void connect(Packet packet) {
		timeouts = 0;
		allowReplacingOfClient(true);
		isConnected = server? handshakeServer(this, packet) : handshakeClient(this, packet);
		allowReplacingOfClient(false);
	}
	@Override
	public void disconnect() {
		isConnected = false;
	}
	
	@Override
	public boolean read(Packet packet) {
		if(!isOpened()) return false;
		try {
			DatagramPacket recp = new DatagramPacket(data, data.length);
			socket.receive(recp);
			
			if(server && ((portOut < 0 && outInet == null) || replace)){
				outInet = recp.getAddress();
				portOut = recp.getPort();
			}
			
			lastRead = FlashUtil.millis();
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
	
	@Override
	public void setReadTimeout(long millis) {
		try {
			socket.setSoTimeout((int)millis);
		} catch (SocketException e) {}
	}
	@Override
	public long getTimeout() {
		try {
			return socket.getSoTimeout();
		} catch (SocketException e) {}
		return -1;
	}
	
	@Override
	public void write(byte[] data) {
		write(data, outInet, portOut);
	}
	@Override
	public void write(byte[] data, int start, int length) {
		if(!isOpened()) return;
		write(Arrays.copyOfRange(data, start, length + start));
	}
	public void write(byte[] data, InetAddress outInet, int portOut){
		if(!isOpened()) return;
		try {
			socket.send(new DatagramPacket(data, data.length, outInet, portOut));
		} catch (IOException e) {}
	}
	
	public void update(long millis){
		if(millis - lastRead >= connectionTimeout){
			timeouts++;
			lastRead = millis;
			timeLastTimeout = millis;
			System.out.println("Timeout");
		}
		if(timeouts >= maxTimeouts){
			System.out.println("Max timeouts");
			isConnected = false;
		}
		if(timeouts > 0 && timeLastTimeout != -1 && 
				millis - timeLastTimeout > (connectionTimeout*3)){
			timeouts = 0;
			timeLastTimeout = -1;
			System.out.println("Timeout reset");
		}
		writeHandshake();
	}

	@Override
	public void setMaxBufferSize(int bytes) {
		data = new byte[bytes];
	}
	@Override
	public int getMaxBufferSize() {
		return data.length;
	}
	
	public void setMaxTimeoutsCount(int timeouts){
		maxTimeouts = timeouts;
	}
	public int getMaxTimeoutsCount(){
		return maxTimeouts;
	}
	public void setConnectionTimeout(int timeout){
		connectionTimeout = timeout;
	}
	public int getConnectionTimeout(){
		return connectionTimeout;
	}
	
	public boolean boundAsServer(){
		return server;
	}
	public int getLocalPort(){
		return socket.getLocalPort();
	}
	public int getRemotePort(){
		return portOut;
	}
	public InetAddress getRemoteAddress(){
		return outInet;
	}
	
	public void allowReplacingOfClient(boolean re){
		replace = re;
	}
	
	@Override
	public boolean isOpened() {
		return !closed;
	}
	@Override
	public boolean isConnected() {
		return isConnected;
	}

	
	private void writeHandshake(){
		write(HANDSHAKE);
	}
	
	private static boolean handshakeServer(ReadInterface readInterface, Packet packet){
		readInterface.setReadTimeout(READ_TIMEOUT * 4);
		readInterface.read(packet);
		if(!isHandshakeClient(packet.data, packet.length))
			return false;
		
		readInterface.write(HANDSHAKE_CONNECT_SERVER);
		readInterface.read(packet);
		if(!isHandshakeClient(packet.data, packet.length))
			return false;
		return true;
	}
	private static boolean handshakeClient(ReadInterface readInterface, Packet packet){
		readInterface.setReadTimeout(READ_TIMEOUT);
		readInterface.write(HANDSHAKE_CONNECT_CLIENT);
		
		readInterface.read(packet);
		if(!isHandshakeServer(packet.data, packet.length))
			return false;
		
		readInterface.write(HANDSHAKE_CONNECT_CLIENT);
		return true;
	}
	public static boolean isHandshake(byte[] bytes, int length){
		if(length != HANDSHAKE.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != HANDSHAKE[i])
				return false;
		}
		return true;
	}
	public static boolean isHandshakeServer(byte[] bytes, int length){
		if(length != HANDSHAKE_CONNECT_SERVER.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != HANDSHAKE_CONNECT_SERVER[i])
				return false;
		}
		return true;
	}
	public static boolean isHandshakeClient(byte[] bytes, int length){
		if(length != HANDSHAKE_CONNECT_CLIENT.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != HANDSHAKE_CONNECT_CLIENT[i])
				return false;
		}
		return true;
	}
}
