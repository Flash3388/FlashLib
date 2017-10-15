package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.frc.FRCHIDInterface;
import edu.flash3388.flashlib.robot.frc.FRCRobot;

/**
 * To allow FlashLib to work with several different platforms, This class provides assistance based on the platform in
 * question. When FlashLib is initialized, it sets the implementation which then allows users calling functions to 
 * get the result according to the platform.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public final class RobotFactory {
	private RobotFactory(){}
	
	private static RobotInterface robotImpl;
	private static HIDInterface hidImpl;
	
	/**
	 * Creates a {@link RobotInterface} implementation to be used for FRC robots.
	 * @return a {@link RobotInterface} implementation for FRC
	 */
	public static RobotInterface createFRCImplementation(){
		return new FRCRobot();
	}
	
	/**
	 * Sets the implementation of {@link RobotInterface} stored by this factory. This implementation will
	 * be used throughout the robot code to access the {@link HIDInterface} provided and the {@link Scheduler}
	 * provided.
	 * @param robot implementation
	 */
	public static void setImplementation(RobotInterface robot){
		robotImpl = robot;
	}
	/**
	 * Gets the {@link RobotInterface} implementation stored by this factory. This implementation will
	 * be used throughout the robot code to access robot related data.
	 * @return implementation
	 */
	public static RobotInterface getImplementation(){
		return robotImpl;
	}
	/**
	 * Gets whether or not a {@link RobotInterface} implementation is stored by this factory. This implementation will
	 * be used throughout the robot code to access robot related data.
	 * @return true if an implementation exists, false otherwise
	 */
	public static boolean hasImplementation(){
		return robotImpl != null;
	}
	
	/**
	 * Creates an {@link HIDInterface} implementation to be used for FRC robots.
	 * @return {@link HIDInterface} implementation for FRC
	 */
	public static HIDInterface createFRCHIDInterface(){
		return new FRCHIDInterface();
	}
	
	/**
	 * Sets the {@link HIDInterface} implementation stored by this factory. This implementation will
	 * be used throughout the robot code to access HID data.
	 * @param hid implementation
	 */
	public static void setHIDInterface(HIDInterface hid){
		hidImpl = hid;
	}
	/**
	 * Gets the {@link HIDInterface} implementation stored by this factory. This implementation will
	 * be used throughout the robot code to access HID data.
	 * @return implementation
	 */
	public static HIDInterface getHIDInterface(){
		return hidImpl;
	}
	/**
	 * Gets whether or not a {@link HIDInterface} implementation is stored by this factory. This implementation will
	 * be used throughout the robot code to access HID data.
	 * @return true if an implementation is available, false otherwise
	 */
	public static boolean hasHIDInterface(){
		return hidImpl != null;
	}
}
