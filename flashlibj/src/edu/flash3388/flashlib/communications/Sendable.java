package edu.flash3388.flashlib.communications;

public abstract class Sendable {	
	private static int nextID = 0;
	private int id;
	private String name;
	private byte type;
	private boolean init = false, attached = false;
	
	protected Sendable(byte type){
		this("", nextID++, type);
	}
	protected Sendable(String name, byte type){
		this(name, nextID++, type);
	}
	protected Sendable(String name, int id, byte type){
		this.id = id;
		this.type = type;
		this.name = name;
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
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public int getID(){
		return id;
	}
	public byte getType(){
		return type;
	}
	public boolean attached(){
		return attached;
	}
	
	public abstract void newData(byte[] data);
	public abstract byte[] dataForTransmition();
	public abstract boolean hasChanged();
	public abstract void onConnection();
	public abstract void onConnectionLost();
}
