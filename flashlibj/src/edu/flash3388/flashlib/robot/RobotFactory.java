package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.frc.FRCRobot;

/**
 * To allow FlashLib to work with several different platforms, This class provides assistance based on the platform in
 * question. When FlashLib is initialized, it sets the implementation which than allows users calling functions to get the result according to the platform.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public final class RobotFactory {
	private RobotFactory(){}

	private static Robot robotImpl;
	
	public static Robot createFRCImplementation(){
		return new FRCRobot();
	}
	
	public static void setImplementation(Robot robot){
		robotImpl = robot;
	}
	public static Robot getImplementation(){
		return robotImpl;
	}
}
