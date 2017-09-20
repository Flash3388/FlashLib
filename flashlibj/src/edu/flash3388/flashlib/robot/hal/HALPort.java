package edu.flash3388.flashlib.robot.hal;

public abstract class HALPort {

	public static final int HAL_INVALID_HANDLE = -1;
	
	protected int handle;

	@Override
	protected void finalize() throws Throwable {
		free();
	}
	public int getHandle(){
		return handle;
	}
	
	public abstract void free();
}
