package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.util.Resource;

/**
 * A base for FlashLib's Hardware Abstraction Layer ports.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class HALResource implements Resource {

	/**
	 * Indicates an invalid HAL port handle. This value is
	 * returned when the current HAL implementation was unable 
	 * to initialize a resource.
	 */
	public static final int HAL_INVALID_HANDLE = -1;
	
	protected int handle = HAL_INVALID_HANDLE;

	@Override
	protected void finalize() throws Throwable {
		free();
	}
	
	/**
	 * Gets the HAL handle which points to this HAL resource.
	 * 
	 * @return the HAL handle.
	 */
	public int getHandle(){
		return handle;
	}
	
	/**
	 * Frees the HAL port. Should be used when usage of the port was done 
	 * in order to free resources.
	 */
	@Override
	public abstract void free();
}
