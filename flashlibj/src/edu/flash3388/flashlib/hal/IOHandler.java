package edu.flash3388.flashlib.hal;

public abstract class IOHandler {

	private int handle;
	
	protected int getHandle(){
		return handle;
	}
	protected void setHandle(int handle){
		this.handle = handle;
	}
	
	public abstract void free();
	
	@Override
	protected void finalize() throws Throwable {
		free();
		super.finalize();
	}
}
