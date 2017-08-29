package edu.flash3388.flashlib.communications;

/**
 * A Sendable is a communication object which communications with a remote counterpart through the communication management
 * system provided by {@link Communications}.
 *  
 * <p>
 * A sendable is described by its ID - a manually defined value or automatically generated value that allows
 * to diffrentiate between different sendables and connect only the matching ones and type - 
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
	private boolean init = false, attached = false;
	
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
		return init;
	}
	void setRemoteInit(boolean b){
		init = b;
	}
	void setAttached(boolean attached){
		this.attached = attached;
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
		return attached;
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
	 * Called when the communications system has connected to a remote system. If this object was attached when connection
	 * was already established, this method will be called as well.
	 */
	public abstract void onConnection();
	/**
	 * Called when the communications system has lost connection with a remote system.
	 */
	public abstract void onConnectionLost();
}
