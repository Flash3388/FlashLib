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
 * between to {@link Sendable} objects, each pair having its own conversation without caring about other sendables. This method
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
 * called. The thread will handle continuous connection to the remote system.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Communications {
	private static class CommTask implements Runnable{
		private Communications comm;
		
		public CommTask(Communications comm){
			this.comm = comm;
		}
		
		@Override
		public void run() {
			while(!Thread.interrupted()){
				FlashUtil.getLog().log("Openning CommInterface", comm.logName);
				while(!comm.open() && !Thread.interrupted())
					FlashUtil.delay(500);
				if(Thread.interrupted()) break;
				
				FlashUtil.getLog().log("Searching for remote connection", comm.logName);
				while(!comm.connect() && !Thread.interrupted())
					FlashUtil.delay(500);
				if(Thread.interrupted()) break;
				
				FlashUtil.getLog().log("Connected", comm.logName);
				comm.resetAll();
				
				while(comm.isConnected() && !Thread.interrupted()){
					comm.sendAll();
					comm.read();
					
					comm.commInterface.update(FlashUtil.millisInt());
					FlashUtil.delay(10);
				}
				comm.onDisconnect();
				FlashUtil.getLog().log("Disconnected", comm.logName);
			}
		}
	}
	
	private static final int MINIMUM_DATA_BYTES = 5;
	
	private static final byte SENDABLE_INIT = 0x0;
	private static final byte SENDABLE_DATA = 0x1;
	private static final byte SENDABLE_CONFIRM_ATTACH = 0x7;
	private static final byte SENDABLE_CONFIRM_DETACH = 0x8;
	
	private Vector<Sendable> attachedSendables;
	private Map<Integer, Sendable> sendables;
	
	private int readStart = -1;
	private boolean allowConnection = true;
	private int nextId, minIdAlloc = 0;
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
	}	
	/**
	 * Creates a new communications management instance which uses a {@link CommInterface} for data transfer and receive.
	 * To start the communications thread, it is necessary to call {@link #start()}. Uses a generated name for logging data.
	 * 
	 * @param readIn communications interface
	 */
	public Communications(CommInterface readIn){
		this("Communications", readIn);
	}
	
	private void initializeConcurrency(String name){
		commTask = new CommTask(this);
		commThread = new Thread(commTask, name+"-Communications");
	}

	private void read(){//ID|VALUE
		if(!isConnected()) return;
		readStart = FlashUtil.millisInt();
		while(!readTimedout()){
			byte[] packet = commInterface.read();
			if(packet == null || packet.length < 1)
				return;
			if(packet.length < MINIMUM_DATA_BYTES)
				continue;
			
			byte dataType = packet[0];
			int id = FlashUtil.toInt(packet, 1);
			Sendable sen = getFromAllAttachedByID(id);
			if(dataType == SENDABLE_DATA && sen != null && sen.remoteAttached() && 
					packet.length - 6 > 0){
				sen.newData(Arrays.copyOfRange(packet, 6, packet.length));
			}else if(dataType == SENDABLE_INIT){
				if(sen == null){
					String str = new String(packet, 6, packet.length - 6);
					createSendable(str, id, packet[5]);
				}else{
					onRemoteAttached(sen, true);
				}
			}else if(dataType == SENDABLE_CONFIRM_ATTACH && sen != null){
				onRemoteAttached(sen, false);
			}else if(dataType == SENDABLE_CONFIRM_DETACH && sen != null && sen.remoteAttached()){
				sen.setRemoteAttached(false);
				sen.onConnectionLost();
			}
		}
	}
	
	private void createSendable(String name, int id, byte type){
		if(sendableCreator == null) return;
		
		Sendable s = sendableCreator.create(name, type);
		if(s != null){
			s.setID(id);
			s.setAttached(true);
			s.setRemoteInit(true);
			sendables.put(id, s);
			
			onRemoteAttached(s, true);
		}
	}
	private void resetAll(){
		nextId = minIdAlloc;
		sendables.clear();
		Enumeration<Sendable> sendablesEnum = attachedSendables.elements();
		while (sendablesEnum.hasMoreElements()) {
			Sendable sendable = sendablesEnum.nextElement();
			if(!sendable.userID()){
				calculateNextID();
				sendable.setID(nextId++);
			}
			sendables.put(sendable.getID(), sendable);
			resetSendable(sendable);
		}
	} 
	private void calculateNextID(){
		while(getLocalyAttachedByID(nextId) != null || getFromAllAttachedByID(nextId) != null)
			++nextId;
	}
	private void resetSendable(Sendable sen){
		sen.setRemoteInit(false);
		sen.setRemoteAttached(false);
	}
	private void onDisconnect(){
		Iterator<Sendable> sendablesEnum = sendables.values().iterator();
		while(sendablesEnum.hasNext())
			handleDisconnection(sendablesEnum.next());
	} 
	private void handleDisconnection(Sendable sen){
		if(sen.remoteAttached()){
			sen.setRemoteAttached(false);
			sen.onConnectionLost();
			confirmRemoteDetached(sen.getID());
		}
		if(!sen.userID())
			sen.setID(-1);
	}
	private void onRemoteAttached(Sendable sendable, boolean confirmRemote){
		if(sendable.remoteAttached()) return;
		
		sendable.setRemoteAttached(true);
		sendable.onConnection();
		if(confirmRemote)
			confirmRemoteAttached(sendable.getID());
	}
	private void confirmRemoteAttached(int id){
		byte[] data = new byte[5];
		data[0] = SENDABLE_CONFIRM_ATTACH;
		FlashUtil.fillByteArray(id, 1, data);
		write(data);
	}
	private void confirmRemoteDetached(int id){
		byte[] data = new byte[5];
		data[0] = SENDABLE_CONFIRM_DETACH;
		FlashUtil.fillByteArray(id, 1, data);
		write(data);
	}
	private void sendAll(){
		Iterator<Sendable> sendablesEnum = sendables.values().iterator();
		while(sendablesEnum.hasNext())
			sendFromSendable(sendablesEnum.next());
	}
	private void sendFromSendable(Sendable sen){
		if(!sen.remoteInit()){
			byte[] bytes = sen.getName().getBytes();
			send(bytes, sen, SENDABLE_INIT);
			sen.setRemoteInit(true);
			return;
		}
		if(!sen.remoteAttached())
			return;
		
		byte[] dataB;
		if(!sen.hasChanged() || (dataB = sen.dataForTransmition()) == null) 
			return;
		send(dataB, sen, SENDABLE_DATA);
	}
	private boolean readTimedout(){
		return readStart != -1 && FlashUtil.millisInt() - readStart > CommInterface.READ_TIMEOUT;
	}
	private void write(byte[] bytes){
		if(!isConnected()) return;
		commInterface.write(bytes);
	}
	
	private void send(byte[] data, Sendable sendable, byte type){
		byte[] bytes = new byte[data.length + 6];
		bytes[0] = type;
		FlashUtil.fillByteArray(sendable.getID(), 1, bytes);
		bytes[5] = sendable.getType();
		System.arraycopy(data, 0, bytes, 6, data.length);
		write(bytes);
	}
	
	private boolean open(){
		if(commInterface.isOpened())
			return true;
		commInterface.open();
		return commInterface.isOpened();
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
		if(!sendable.remoteAttached())
			throw new RuntimeException("Sendable is not connected to a remote sendable");
		
		send(data, sendable, SENDABLE_DATA);
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
	 * {@link IllegalStateException} is thrown. 
	 * </p>
	 * 
	 * @param sendable sendable to attach
	 * 
	 * @throws IllegalStateException if the sendable is already attached
	 */
	public void attach(Sendable sendable){
		if(sendable.attached())
			throw new IllegalStateException("Sendable is already attached to communications");
			
		attachedSendables.add(sendable);
		sendable.setAttached(true);
		sendable.setUserID(false);
		if(isConnected()){
			calculateNextID();
			sendable.setID(nextId++);
			sendables.put(sendable.getID(), sendable);
			resetSendable(sendable);
		}else
			sendable.setID(-1);
	}
	/**
	 * Attaches a {@link Sendable} object to this communications manager and forces an ID for it instead
	 * of an automatically allocated ID. If the object is already attached to a communications manager (i.e.
	 * {@link Sendable#attached()} returns true) this will fail. If the ID is already allocated to a different object,
	 * this will fail.
	 * 
	 * 
	 * @param sendable the object
	 * @param id the id to allocate
	 * 
	 * @throws IllegalStateException if the sendable is attached already or the ID requested is already allocated
	 */
	public void attachForID(Sendable sendable, int id){
		if(sendable.attached())
			throw new IllegalStateException("Sendable is already attached to communications");
		if(getLocalyAttachedByID(id) != null)
			throw new IllegalStateException("Sendable with such ID already exists");
		if(getFromAllAttachedByID(id) != null)
			throw new IllegalStateException("Sendable with such ID was dynamically created");
		
		sendable.setID(id);
		attachedSendables.add(sendable);
		sendable.setAttached(true);
		sendable.setUserID(true);
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
			sendable.setAttached(false);
			if(isConnected()){
				handleDisconnection(sendable);
				sendables.remove(sendable.getID());
			}
			return true;
		}
		return false;
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
	 * Attempts connection to a remote communications object. This is done by calling {@link CommInterface#connect()}.
	 * If connection is not allowed, the method will simply return false.
	 * @return true if connection was established, false otherwise
	 */
	public boolean connect(){
		if(isConnected()) return true;
		if(!allowConnection) return false;
		
		commInterface.connect();
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
	 * Sets the minimum ID to be used when allocating IDs for attached sendables.
	 * It is a good idea to set this value when both communication sides might attach
	 * sendables, thus allowing to avoid id collisions between sendables.
	 * <p>
	 * The default is 0.
	 * 
	 * @param minId min id value
	 */
	public void setMinAllocationID(int minId){
		if(minId < 0)
			throw new IllegalArgumentException("ID must be non-negative");
		this.minIdAlloc = minId;
	}
	/**
	 * Gets the minimum ID to be used when allocating IDs for attached sendables.
	 * It is a good idea to set this value when both communication sides might attach
	 * sendables, thus allowing to avoid id collisions between sendables.
	 * <p>
	 * The default is 0.
	 * 
	 * @return min id value
	 */
	public int getMinAllocationID(){
		return minIdAlloc;
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
		commThread.interrupt();
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
