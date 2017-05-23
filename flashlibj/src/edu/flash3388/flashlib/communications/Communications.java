package edu.flash3388.flashlib.communications;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;

public class Communications {
	private static class CommTask implements Runnable{
		boolean stop = false;
		private Communications comm;
		
		public CommTask(Communications comm){
			this.comm = comm;
		}
		
		@Override
		public void run() {
			try{
				while(!stop){
					FlashUtil.getLog().log("Searching for remote connection", comm.logName);
					while(!comm.connect() && !stop);
					if(stop) break;
					
					FlashUtil.getLog().log("Connected", comm.logName);
					comm.resetAll();
					
					while(comm.isConnected()){
						comm.sendAll();
						comm.read();
						
						comm.commInterface.update(FlashUtil.millis());
						FlashUtil.delay(10);
					}
					comm.onDisconnect();
					FlashUtil.getLog().log("Disconnected", comm.logName);
				}
			}catch(IOException e){
				FlashUtil.getLog().reportError(e.getMessage());
				comm.disconnect();
			}
		}
		public void stop(){
			stop = true;
		}
	}
	
	public static final short MAX_REC_LENGTH = 100;
	
	private static byte instances = 0;
	
	private Vector<Sendable> sendables, attachedSendables;
	
	private long currentMillis = -1, readStart = -1;
	private int readTimeout;
	private Packet packet = new Packet();
	private CommInterface commInterface;
	private SendableCreator sendableCreator;
	private String name, logName;
	
	private Thread commThread;
	private CommTask commTask;
	
	public Communications(String name, CommInterface readIn){
		instances++;
		this.name = name;
		this.commInterface = readIn;
		this.logName = name+"-Comm";
		
		initializeConcurrency();
		FlashUtil.getLog().log("Initialized", logName);
		
		sendables = new Vector<Sendable>();
		attachedSendables = new Vector<Sendable>();
		setBufferSize(MAX_REC_LENGTH);
		commInterface.open();
	}
	public Communications(CommInterface readIn){
		this(""+instances, readIn);
	}
	
	private void initializeConcurrency(){
		commTask = new CommTask(this);
		commThread = new Thread(commTask, name+"-Communications");
	}
	private Packet receivePacket(){
		if(!commInterface.read(packet))
			return null;
		return packet;
	}
	private void read(){//ID|VALUE
		if(!isConnected()) return;
		updateClock();
		readStart = currentMillis;
		while(!readTimedout()){
			Packet packet = receivePacket();
			if(packet == null || packet.length < 1)
				return;
			if(packet.length < 5)
				continue;
			
			int id = FlashUtil.toInt(packet.data);
			Sendable sen = getByID(id);
			if(sen != null)
				sen.newData(Arrays.copyOfRange(packet.data, 5, packet.length));
			else if(sendableCreator != null){
				String str = new String(packet.data, 5, packet.length - 5);
				sen = sendableCreator.create(str, id, packet.data[4]);
				if(sen != null){
					sendables.add(sen);
					sen.setAttached(true);
				}
			}
		}
	}
	private void resetAll(){
		sendables.clear();
		Enumeration<Sendable> sendablesEnum = attachedSendables.elements();
		while (sendablesEnum.hasMoreElements()) {
			Sendable sendable = sendablesEnum.nextElement();
			sendables.addElement(sendable);
			resetSendable(sendable);
		}
	} 
	private void resetSendable(Sendable sen){
		sen.setRemoteInit(false);
		sen.onConnection();
	}
	private void onDisconnect(){
		Enumeration<Sendable> sendablesEnum = sendables.elements();
		while(sendablesEnum.hasMoreElements())
			handleDisconnection(sendablesEnum.nextElement());
	} 
	private void handleDisconnection(Sendable sen){
		sen.onConnectionLost();
	}
	private void sendAll(){
		Enumeration<Sendable> sendablesEnum = sendables.elements();
		while(sendablesEnum.hasMoreElements())
			sendFromSendable(sendablesEnum.nextElement());
	}
	private void sendFromSendable(Sendable sen){
		if(!sen.remoteInit()){
			byte[] bytes = sen.getName().getBytes();
			send(bytes, sen);
			sen.setRemoteInit(true);
			return;
		}
		
		byte[] dataB;
		if(!sen.hasChanged() || (dataB = sen.dataForTransmition()) == null) 
			return;
		send(dataB, sen);
	}
	private void updateClock(){
		currentMillis = FlashUtil.millis();
	}
	private boolean readTimedout(){
		updateClock();
		return readStart != -1 && currentMillis - readStart > readTimeout;
	}
	private void write(byte[] bytes){
		if(!isConnected()) return;
		commInterface.write(bytes);
	}
	
	private void send(byte[] data, Sendable sendable){
		byte[] bytes = new byte[data.length + 5];
		FlashUtil.fillByteArray(sendable.getID(), bytes);
		bytes[4] = sendable.getType();
		System.arraycopy(data, 0, bytes, 5, data.length);
		write(bytes);
	}
	
	public void attach(Sendable... sendables){
		for (Sendable sendable : sendables) 
			attach(sendable);
	}
	public void attach(Sendable sendable){
		if(getByID(sendable.getID()) == null){
			attachedSendables.add(sendable);
			sendable.setAttached(true);
			if(isConnected()){
				sendables.addElement(sendable);
				resetSendable(sendable);
			}
		}
	}
	public boolean detach(Sendable sendable){
		if(sendables.remove(sendable)){
			attachedSendables.remove(sendable);
			sendable.setAttached(false);
			if(isConnected())
				handleDisconnection(sendable);
		}
		return !sendable.attached();
	}
	public boolean detach(int index){
		Sendable sen = sendables.get(index);
		if(sen != null) {
			attachedSendables.remove(sen);
			sendables.remove(index);
			sen.setAttached(false);
			if(isConnected())
				handleDisconnection(sen);
		}
		return sen != null && !sen.attached();
	}
	public boolean detachByID(int id){
		Sendable sen = getByID(id);
		if(sen != null) {
			attachedSendables.remove(sen);
			sendables.remove(sen);
			sen.setAttached(false);
			if(isConnected())
				handleDisconnection(sen);
		}
		return sen != null && !sen.attached();
	}
	public void detachAll(){
		Enumeration<Sendable> sendablesEnum = sendables.elements();
		while (sendablesEnum.hasMoreElements())
			detach(sendablesEnum.nextElement());
	}
	public Sendable getByID(int id){
		Enumeration<Sendable> sendablesEnum = sendables.elements();
		while(sendablesEnum.hasMoreElements()){
			Sendable sen = sendablesEnum.nextElement();
			if(sen.getID() == id)
				return sen;
		}
		return null;
	}
	public String getName(){
		return name;
	}
	public CommInterface getCommInterface(){
		return commInterface;
	}
	public boolean connect() throws IOException{
		if(isConnected()) return true;
		updateClock();
		commInterface.connect(packet);
		if(isConnected()){
			setReadTimeout(CommInterface.READ_TIMEOUT);
			return true;
		}
		return false;
	}
	public void disconnect(){
		if(isConnected()){
			commTask.stop();
			commInterface.disconnect();
		}
	}
	public boolean isConnected(){
		return commInterface.isConnected();
	}
	public void setReadTimeout(int timeout){
		readTimeout = timeout;
		commInterface.setReadTimeout(readTimeout);
	}
	public int getReadTimeout(){
		return readTimeout;
	}
	public void setSendableCreator(SendableCreator creator){
		sendableCreator = creator;
	}
	public void setBufferSize(int size){
		commInterface.setMaxBufferSize(size);
	}
	public SendableCreator getSendableCreator(){
		return sendableCreator;
	}
	public void start(){
		if(!commThread.isAlive())
			commThread.start();
	}
	public void close() {
		disconnect();
		commInterface.close();
		detachAll();
	}
	public void sendDataForSendable(Sendable sendable, byte[] data){
		if(getByID(sendable.getID()) == null)
			return;
		send(data, sendable);
	}
	
	
	public static boolean handshakeServer(CommInterface commInterface, Packet packet){
		commInterface.setReadTimeout(CommInterface.READ_TIMEOUT * 4);
		commInterface.read(packet);
		if(!isHandshakeClient(packet.data, packet.length))
			return false;
		
		commInterface.write(CommInterface.HANDSHAKE_CONNECT_SERVER);
		commInterface.read(packet);
		if(!isHandshakeClient(packet.data, packet.length))
			return false;
		return true;
	}
	public static boolean handshakeClient(CommInterface commInterface, Packet packet){
		commInterface.setReadTimeout(CommInterface.READ_TIMEOUT);
		commInterface.write(CommInterface.HANDSHAKE_CONNECT_CLIENT);
		
		commInterface.read(packet);
		if(!isHandshakeServer(packet.data, packet.length))
			return false;
		
		commInterface.write(CommInterface.HANDSHAKE_CONNECT_CLIENT);
		return true;
	}
	public static boolean isHandshake(byte[] bytes, int length){
		if(length != CommInterface.HANDSHAKE.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != CommInterface.HANDSHAKE[i])
				return false;
		}
		return true;
	}
	public static boolean isHandshakeServer(byte[] bytes, int length){
		if(length != CommInterface.HANDSHAKE_CONNECT_SERVER.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != CommInterface.HANDSHAKE_CONNECT_SERVER[i])
				return false;
		}
		return true;
	}
	public static boolean isHandshakeClient(byte[] bytes, int length){
		if(length != CommInterface.HANDSHAKE_CONNECT_CLIENT.length) return false;
		for(int i = 0; i < length; i++){
			if(bytes[i] != CommInterface.HANDSHAKE_CONNECT_CLIENT[i])
				return false;
		}
		return true;
	}
}
