package edu.flash3388.flashlib.robot;

/**
 * An interface for the current robot implementation. Provides for data about the robot.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public interface Robot {
	
	HIDInterface getHIDInterface();
	
	boolean isDisabled();
	
	boolean isOperatorControl();
	
	boolean isFRC();
}
