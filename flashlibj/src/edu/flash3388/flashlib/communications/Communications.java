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
						
						comm.readInterface.update(FlashUtil.millis());
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
	
	public static final int READ_TIMEOUT = 20;
	public static final int MAX_REC_LENGTH = 100;
	
	private static int instances = 0;
	
	private Vector<Sendable> sendables;
	
	private long currentMillis = -1, readStart = -1;
	private int readTimeout;
	private Packet packet = new Packet();
	private ReadInterface readInterface;
	private SendableCreator sendableCreator;
	private String name, logName;
	
	private Thread commThread;
	private CommTask commTask;
	
	public Communications(String name, ReadInterface readIn){
		instances++;
		this.name = name;
		this.readInterface = readIn;
		this.logName = name+"-Comm";
		
		initializeConcurrency();
		FlashUtil.getLog().log("Initialized", logName);
		
		sendables = new Vector<Sendable>();
		setBufferSize(MAX_REC_LENGTH);
		readInterface.open();
	}
	public Communications(ReadInterface readIn){
		this(""+instances, readIn);
	}
	
	private void initializeConcurrency(){
		commTask = new CommTask(this);
		commThread = new Thread(commTask, name+"-Communications");
	}
	private Packet receivePacket(){
		if(!readInterface.read(packet))
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
		Enumeration<Sendable> sendablesEnum = sendables.elements();
		while(sendablesEnum.hasMoreElements())
			resetSendable(sendablesEnum.nextElement());
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
		readInterface.write(bytes);
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
			sendables.add(sendable);
			sendable.setAttached(true);
			if(isConnected())
				resetSendable(sendable);
		}
	}
	public boolean detach(Sendable sendable){
		if(sendables.remove(sendable)){
			sendable.setAttached(false);
			if(isConnected())
				handleDisconnection(sendable);
		}
		return !sendable.attached();
	}
	public boolean detach(int index){
		Sendable sen = sendables.get(index);
		if(sen != null) {
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
	public ReadInterface getReadInterface(){
		return readInterface;
	}
	public boolean connect() throws IOException{
		if(isConnected()) return true;
		updateClock();
		readInterface.connect(packet);
		if(isConnected()){
			setReadTimeout(READ_TIMEOUT);
			return true;
		}
		return false;
	}
	public void disconnect(){
		if(isConnected()){
			commTask.stop();
			readInterface.disconnect();
		}
	}
	public boolean isConnected(){
		return readInterface.isConnected();
	}
	public void setReadTimeout(int timeout){
		readTimeout = timeout;
		readInterface.setReadTimeout(readTimeout);
	}
	public int getReadTimeout(){
		return readTimeout;
	}
	public void setSendableCreator(SendableCreator creator){
		sendableCreator = creator;
	}
	public void setBufferSize(int size){
		readInterface.setMaxBufferSize(size);
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
		readInterface.close();
		detachAll();
	}
	public void sendDataForSendable(Sendable sendable, byte[] data){
		if(getByID(sendable.getID()) == null)
			return;
		send(data, sendable);
	}
}
