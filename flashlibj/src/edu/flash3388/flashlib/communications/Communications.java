package edu.flash3388.flashlib.communications;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Provides a communications management system between to sides. The communications is split into mini communication part
 * between to {@link Sendable} objects, each pair having its own conversion without caring about other sendables. This method
 * allows for managing of multiple information types through a single communications port, resulting in a concurrent data 
 * communication system. To allow for flexible communications system, this classes uses a {@link CommInterface} object for 
 * reading and sending data.
 *
 * <p>
 * When attached using {@link #attach(Sendable)}, a sendable is added to a collection of locally attached sendables. Upon
 * connection with another {@link Communications} object, the sendables are transfered to a map. Data about the sendables 
 * is than sent to the remote side, where if a sendable with a matching ID exists than they begin conversion, otherwise
 * a matching sendable is created using a {@link SendableCreator} object, if one exists ({@link #setSendableCreator(SendableCreator)}),
 * and added to the sendable map. Upon disconnection, the map is purged of sendables, but the collection of locally attached sendables
 * remains intact.
 * </p>
 * 
 * <p>
 * Communication is managed in a thread, which starts when {@link #start()} is called, and terminated when {@link #close()} is
 * called. The thread will handle continuos connection to the remote system.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Communications {
	private static class CommTask implements Runnable{
		boolean stop = false;
		private Communications comm;
		
		public CommTask(Communications comm){
			this.comm = comm;
		}
		
		@Override
		public void run() {
			while(!stop){
				FlashUtil.getLog().log("Searching for remote connection", comm.logName);
				while(!comm.connect() && !stop)
					FlashUtil.delay(50);
				if(stop) break;
				
				FlashUtil.getLog().log("Connected", comm.logName);
				comm.resetAll();
				
				while(comm.isConnected() && !stop){
					comm.sendAll();
					comm.read();
					
					comm.commInterface.update(FlashUtil.millisInt());
					FlashUtil.delay(10);
				}
				comm.onDisconnect();
				FlashUtil.getLog().log("Disconnected", comm.logName);
			}
		}
		public void stop(){
			stop = true;
		}
	}
	
	private static byte instances = 0;
	
	private Vector<Sendable> attachedSendables;
	private Map<Integer, Sendable> sendables;
	
	private long currentMillis = -1, readStart = -1;
	private int readTimeout;
	private boolean allowConnection = true;
	private Packet packet = new Packet();
	private CommInterface commInterface;
	private SendableCreator sendableCreator;
	private String logName;
	
	private Thread commThread;
	private CommTask commTask;
	
	/**
	 * Creates a new communications management instance which uses a {@link CommInterface} for data transfer and receive.
	 * To start the communications thread, it is necessary to call {@link #start()}.
	 * 
	 * @param name logging name
	 * @param readIn communications interface
	 */
	public Communications(String name, CommInterface readIn){
		this.commInterface = readIn;
		this.logName = name+"-Comm";
		
		initializeConcurrency(name);
		FlashUtil.getLog().log("Initialized", logName);
		
		sendables = new ConcurrentHashMap<Integer, Sendable>();
		attachedSendables = new Vector<Sendable>();
		commInterface.open();
	}	
	/**
	 * Creates a new communications management instance which uses a {@link CommInterface} for data transfer and receive.
	 * To start the communications thread, it is necessary to call {@link #start()}. Uses a generated name for logging data.
	 * 
	 * @param readIn communications interface
	 */
	public Communications(CommInterface readIn){
		this(""+(++instances), readIn);
	}
	
	private void initializeConcurrency(String name){
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
			Sendable sen = getFromAllAttachedByID(id);
			if(sen != null && packet.length - 5 > 0){
				sen.newData(Arrays.copyOfRange(packet.data, 5, packet.length));
			}
			else{
				String str = new String(packet.data, 5, packet.length - 5);
				createSendable(str, id, packet.data[4]);
			}
		}
	}
	
	private void createSendable(String name, int id, byte type){
		if(sendableCreator == null) return;
		
		Sendable s = sendableCreator.create(name, id, type);
		if(s != null){
			s.setAttached(true);
			s.setRemoteInit(true);
			sendables.put(s.getID(), s);
		}
	}
	private void resetAll(){
		sendables.clear();
		Enumeration<Sendable> sendablesEnum = attachedSendables.elements();
		while (sendablesEnum.hasMoreElements()) {
			Sendable sendable = sendablesEnum.nextElement();
			sendables.put(sendable.getID(), sendable);
			resetSendable(sendable);
		}
	} 
	private void resetSendable(Sendable sen){
		sen.setRemoteInit(false);
		sen.onConnection();
	}
	private void onDisconnect(){
		Iterator<Sendable> sendablesEnum = sendables.values().iterator();
		while(sendablesEnum.hasNext())
			handleDisconnection(sendablesEnum.next());
	} 
	private void handleDisconnection(Sendable sen){
		sen.onConnectionLost();
	}
	private void sendAll(){
		Iterator<Sendable> sendablesEnum = sendables.values().iterator();
		while(sendablesEnum.hasNext())
			sendFromSendable(sendablesEnum.next());
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
	
	/**
	 * Sends data from a sendable to its remote counter part. If no connection was established, nothing will occur.
	 * If the sendable is not attached to this system, a runtime exception will be thrown. If the sendable was
	 * not initialized, a runtime exception will be thrown.
	 * 
	 * @param sendable the sendable 
	 * @param data byte array of data
	 * 
	 * @throws RuntimeException the sendable is not attached or initialized for remote communications
	 */
	public void sendDataForSendable(Sendable sendable, byte[] data){
		if(!isConnected())
			return;
		if(getFromAllAttachedByID(sendable.getID()) == null)
			throw new RuntimeException("No such id attached");
		if(!sendable.remoteInit())
			throw new RuntimeException("Sendable was not initialized for remote connection");
		
		send(data, sendable);
	}
	
	/**
	 * Attaches an array of sendables. Passes them one by one to {@link #attach(Sendable)}.
	 * @param sendables array of sendables to attach
	 */
	public void attach(Sendable... sendables){
		for (int i = 0; i < sendables.length; i++) 
			attach(sendables[i]);
	}
	/**
	 * Attaches a new sendable to the system. If a connection was already established, than the sendable is
	 * Immediately added to the sendables map for use.
	 * 
	 * <p>
	 * If the sendable is already attached to a communications system, an 
	 * {@link IllegalStateException} is thrown. If a sendable with the same ID is already attached, a 
	 * {@link RuntimeException}.
	 * </p>
	 * 
	 * @param sendable sendable to attach
	 * 
	 * @throws IllegalStateException if the sendable is already attached
	 * @throws RuntimeException if a sendable with the same id is attached
	 */
	public void attach(Sendable sendable){
		if(sendable.attached())
			throw new IllegalStateException("Sendable is already attached to communications");
		if(getFromAllAttachedByID(sendable.getID()) != null)
			throw new RuntimeException("Id taken");
			
		attachedSendables.add(sendable);
		sendable.setAttached(true);
		if(isConnected()){
			sendables.put(sendable.getID(), sendable);
			resetSendable(sendable);
		}
	}
	/**
	 * Removes a locally sendable from the system if it is attached. If connection was already established, 
	 * the sendable is removed from the map.
	 * 
	 * 
	 * @param sendable sendable to remove
	 * @return true if the sendable was removed, false otherwise
	 */
	public boolean detach(Sendable sendable){
		if(attachedSendables.remove(sendable)){
			sendables.remove(sendable.getID());
			sendable.setAttached(false);
			if(isConnected())
				handleDisconnection(sendable);
		}
		return !sendable.attached();
	}
	/**
	 * Removes a locally sendable from the system if it is attached. If connection was already established, 
	 * the sendable is removed from the map.
	 * 
	 * 
	 * @param id id of the sendable to remove
	 * @return true if the sendable was removed, false otherwise
	 */
	public boolean detach(int id){
		Sendable sen = getLocalyAttachedByID(id);
		if(sen != null) {
			attachedSendables.remove(sen);
			sen.setAttached(false);
			if(isConnected()){
				sendables.remove(sen.getID());
				handleDisconnection(sen);
			}
		}
		return sen != null && !sen.attached();
	}
	/**
	 * Removes all the locally attached sendables.
	 */
	public void detachAll(){
		Enumeration<Sendable> sendablesEnum = attachedSendables.elements();
		while (sendablesEnum.hasMoreElements())
			detach(sendablesEnum.nextElement());
	}
	/**
	 * Gets a locally attached sendable by its id, if such exists.
	 * 
	 * @param id id of the sendable to return
	 * @return a sendable with a matching id, if one exists
	 */
	public Sendable getLocalyAttachedByID(int id){
		Enumeration<Sendable> sendablesEnum = attachedSendables.elements();
		while(sendablesEnum.hasMoreElements()){
			Sendable sen = sendablesEnum.nextElement();
			if(sen.getID() == id)
				return sen;
		}
		return null;
	}
	/**
	 * Gets an attached sendable by its id, if such exists. This includes remotely attached sendable as well.
	 * 
	 * @param id id of the sendable to return
	 * @return a sendable with a matching id, if one exists
	 */
	public Sendable getFromAllAttachedByID(int id){
		return sendables.get(id);
	}
	
	/**
	 * Gets the {@link CommInterface} used by this system for communications.
	 * 
	 * @return communications interface used for communications
	 */
	public CommInterface getCommInterface(){
		return commInterface;
	}
	
	/**
	 * Sets whether to allow connection or not. If connection is not allowed, when connection is attempted it will be
	 * aborted.
	 * @param allow true to allow, false otherwise.
	 * @see #connect()
	 */
	public void setAllowConnection(boolean allow){
		allowConnection = allow;
	}
	/**
	 * Gets whether to allow connection or not. If connection is not allowed, when connection is attempted it will be
	 * aborted.
	 * @return true if connection is allowed, false otherwise.
	 * @see #connect()
	 */
	public boolean isConnectionAllowed(){
		return allowConnection;
	}
	
	/**
	 * Attempts connection to a remote communications object. This is done by calling {@link CommInterface#connect(Packet)}.
	 * If connection is not allowed, the method will simply return false.
	 * @return true if connection was established, false otherwise
	 */
	public boolean connect(){
		if(isConnected()) return true;
		if(!allowConnection) return false;
		
		updateClock();
		commInterface.connect(packet);
		if(isConnected()){
			setReadTimeout(CommInterface.READ_TIMEOUT);
			return true;
		}
		return false;
	}
	/**
	 * If connected, attempts disconnection from a remote communications object. Stops the communication loop and 
	 * calls {@link CommInterface#disconnect()}
	 * 
	 * <p>
	 * It is not recommended to call this method explicitly.
	 * </p>
	 */
	public void disconnect(){
		if(isConnected()){
			commTask.stop();
			commInterface.disconnect();
		}
	}
	/**
	 * Gets whether this port is connected to a remote communications port.
	 * @return true if connected, false otherwise.
	 */
	public boolean isConnected(){
		return commInterface.isConnected();
	}
	

	/**
	 * Sets timeout for the reading blocking call in milliseconds.
	 * 
	 * @param timeout timeout for reading call milliseconds
	 */
	public void setReadTimeout(int timeout){
		commInterface.setReadTimeout(timeout);
	}
	/**
	 * Gets the timeout for the reading blocking call in milliseconds.
	 * @return timeout for reading call in milliseconds.
	 */
	public int getReadTimeout(){
		return commInterface.getTimeout();
	}
	
	/**
	 * Sets the {@link SendableCreator} object used to create sendables for a remote communications system.
	 * 
	 * @param creator sendable creator
	 */
	public void setSendableCreator(SendableCreator creator){
		sendableCreator = creator;
	}
	/**
	 * Gets the sendable creator used tor create sendables for a remote communications system.
	 * 
	 * @return the sendable creator
	 */
	public SendableCreator getSendableCreator(){
		return sendableCreator;
	}
	
	/**
	 * Sets the maximum size of the reading data buffer. This is the maximum amount of bytes that can
	 * be read from the port.
	 * 
	 * @param size maximum amount of bytes in the data buffer.
	 */
	public void setBufferSize(int size){
		commInterface.setMaxBufferSize(size);
	}
	/**
	 * Gets the maximum size of the reading data buffer. This is the maximum amount of bytes that can
	 * be read from the port.
	 * @return maximum amount of bytes in the data buffer.
	 */
	public int getBufferSize(){
		return commInterface.getMaxBufferSize();
	}

	/**
	 * Starts the communications thread if it has not started.
	 */
	public void start(){
		if(!commThread.isAlive())
			commThread.start();
	}
	/**
	 * Closes this communications system. Usage of this system will not be possible after closing.
	 * Awaits termination of the communications thread.
	 */
	public void close() {
		disconnect();
		commTask.stop();
		commInterface.close();
		detachAll();
		
		try {
			commThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}
}
