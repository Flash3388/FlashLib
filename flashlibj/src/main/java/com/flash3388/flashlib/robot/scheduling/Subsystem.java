package com.flash3388.flashlib.robot.scheduling;

/**
 * Subsystem is the base for robot systems. When defining a class for a system on a robot, extend this class. 
 * Doing so, allows operation of the system with FlashLib's scheduling system. 
 * <p>
 * A subsystem can be defined as a system on a robot which can be used separately from other parts of the robot. 
 * Examples for subsystems include but are not limited to: drive trains, arms, shooters, etc.
 * The concept of what makes a part of a robot into a subsystem depends on the way you wish
 * to organize you code, but in general remains the same.
 * <p>
 * Each subsystem should have only one instance in our robot code.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Subsystem {
}
