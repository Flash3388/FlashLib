package edu.flash3388.flashlib.robot;

/**
 * 
 * RobotState holds data about the robot's operational state. Used throughout FlashLib to determine whether 
 * certain actions should be done or not. The implementation is set automatically by RobotFactory.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class RobotState {

	private static RobotState impl;
	
	public static void setImplementation(RobotState impl){
		RobotState.impl = impl;
	}
	
	public abstract boolean isDisabled();
	public abstract boolean isTeleop();
	
	/**
	 * Gets whether or not the robot is in disabled state. The implementation can determine this on its on. 
	 * If the implementation is not set, false will be returned.
	 * @return true if the robot is in disabled state according to the implementation.
	 */
	public static boolean isRobotDisabled(){
		return impl != null && impl.isDisabled();
	}
	/**
	 * Gets whether or not the robot is in tele-operation state. The implementation can determine this on its on. 
	 * If the implementation is not set, false will be returned.
	 * @return true if the robot is in tele-operation state according to the implementation.
	 */
	public static boolean isRobotTeleop(){
		return impl != null && impl.isTeleop();
	}
	/**
	 * Gets whether or not the robot is in emergency stop. 
	 * @return true if the robot is in emergency stop, false otherwise
	 * @see FlashRoboUtil#inEmergencyStop()
	 */
	public static boolean inEmergencyStop(){
		return FlashRoboUtil.inEmergencyStop();
	}
}
