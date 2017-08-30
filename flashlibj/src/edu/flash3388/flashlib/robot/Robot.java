package edu.flash3388.flashlib.robot;

/**
 * An interface for the current robot implementation. Provides abstraction for scheduling, states and HID access.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public interface Robot {
	
	Scheduler scheduler();
	
	HIDInterface hid();
	
	boolean isDisabled();
	
	boolean isOperatorControl();
	
	boolean isFRC();
}
