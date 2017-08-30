package edu.flash3388.flashlib.communications;

/**
 * A Sendable is a communication object which communications with a remote counterpart through the communication management
 * system provided by {@link Communications}.
 *  
 * <p>
 * A sendable is described by its ID - an automatically assigned value that allows
 * to differentiate between different sendables and connect only the matching ones, and its type - 
 * a byte which tells the remote communications object what object to create as a counterpart.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Sendable {
	
	//private static int nextID = 0;
	private int id;
	private String name;
	private byte type;
	private boolean remoteAttached = false, communicationsAttached = false;
	private boolean remoteInit = false;
	
	/**
	 * Creates a sendable object with an empty name, automatically defined ID and a type.
	 * 
	 * @param type the type of the sendable
	 */
	protected Sendable(byte type){
		this("", type);
	}
	/**
	 * Creates a sendable object with a name, automatically defined ID and a type.
	 * 
	 * @param name the name of the sendable
	 * @param type the type of the sendable
	 */
	protected Sendable(String name, byte type){
		this(name, 0, type);
	}
	/**
	 * Creates a sendable object with a name, manually defined ID and type.
	 * It is recommended to use this constructor only when creating objects through the {@code SendableCreator}.
	 * 
	 * @param name the name of the sendable
	 * @param id the ID of the sendable
	 * @param type the type of the sendable
	 */
	private Sendable(String name, int id, byte type){
		this.id = id;
		this.type = type;
		this.name = name;
	}
	
	void setId(int id){
		this.id = id;
	}
	boolean remoteInit(){
		return remoteInit;
	}
	void setRemoteInit(boolean b){
		remoteInit = b;
	}
	void setRemoteAttached(boolean attached){
		remoteAttached = attached;
	}
	void setAttached(boolean attached){
		communicationsAttached = attached;
	}
	
	/**
	 * Gets the name of the sendable
	 * @return the name of the sendable
	 */
	public String getName(){
		return name;
	}
	/**
	 * Sets the name of the sendable
	 * @param name the new name of the sendable
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * Gets the ID of the sendable
	 * @return the id of the sendable
	 */
	public int getID(){
		return id;
	}
	/**
	 * Gets the type of the sendable
	 * @return the type of the sendable
	 */
	public byte getType(){
		return type;
	}
	/**
	 * Gets whether or not this sendable is attached to a communications system.
	 * 
	 * @return true if attached, false otherwise
	 */
	public boolean attached(){
		return communicationsAttached;
	}
	/**
	 * Gets whether or not this sendable is connected with a remote sendable
	 * @return true if connected, false otherwise
	 */
	public boolean remoteAttached(){
		return remoteAttached;
	}
	
	/**
	 * Called when new data is received by the communications system for this sendable by its counterpart.
	 * 
	 * @param data the received data
	 */
	public abstract void newData(byte[] data);
	/**
	 * Gets data from this sendable to send to its remote counterpart.
	 * 
	 * @return data to send
	 */
	public abstract byte[] dataForTransmition();
	/**
	 * Gets whether or not this sendable has new data to send to its counterpart. If it does, {@link #dataForTransmition()}
	 * is called and data is sent.
	 * 
	 * @return true if there is data to send.
	 */
	public abstract boolean hasChanged();
	/**
	 * Called when the communications system has connected to a remote system and confirmed connection with a remote sendable
	 * matching the ID of this on. 
	 */
	public abstract void onConnection();
	/**
	 * Called when the communications system has lost connection with the remote sendable communicating with this one.
	 */
	public abstract void onConnectionLost();
}
