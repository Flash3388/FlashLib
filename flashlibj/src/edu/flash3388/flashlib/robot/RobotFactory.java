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
	
	/**
	 * Creates a {@link Robot} implementation to be used for FRC robots.
	 * @return a {@link Robot} implementation for FRC
	 */
	public static Robot createFRCImplementation(){
		return new FRCRobot();
	}
	
	/**
	 * Sets the implementation of {@link Robot} stored by this factory. This implementation will
	 * be used throughout the robot code to access the {@link HIDInterface} provided and the {@link Scheduler}
	 * provided.
	 * @param robot implementation
	 */
	public static void setImplementation(Robot robot){
		robotImpl = robot;
	}
	/**
	 * Gets the {@link Robot} implementation stored by this factory. This implementation will
	 * be used throughout the robot code to access the {@link HIDInterface} provided and the {@link Scheduler}
	 * provided. 
	 * @return implementation
	 */
	public static Robot getImplementation(){
		return robotImpl;
	}
	/**
	 * Gets whether or not a {@link Robot} implementation is stored by this factory. This implementation will
	 * be used throughout the robot code to access the {@link HIDInterface} provided and the {@link Scheduler}
	 * provided. 
	 * @return true if an implementation exists, false otherwise
	 */
	public static boolean hasImplementation(){
		return robotImpl != null;
	}
}
