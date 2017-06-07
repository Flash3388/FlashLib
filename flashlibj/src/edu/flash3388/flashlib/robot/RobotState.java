package edu.flash3388.flashlib.robot;

public abstract class RobotState {

	private static RobotState impl;
	public static void setImplementation(RobotState impl){
		RobotState.impl = impl;
	}
	public static RobotState getInstance(){
		return impl;
	}
	
	public abstract boolean isDisabled();
	public abstract boolean isTeleop();
	
	public static boolean isRobotDisabled(){
		return impl != null && impl.isDisabled();
	}
	public static boolean isRobotTeleop(){
		return impl != null && impl.isTeleop();
	}
	public static boolean inEmergencyStop(){
		return FlashRoboUtil.inEmergencyStop();
	}
}
