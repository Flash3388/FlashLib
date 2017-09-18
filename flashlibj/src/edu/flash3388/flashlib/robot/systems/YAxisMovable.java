package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Subsystem;

/**
 * Interface for object with the capability to move along the y-axis.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface YAxisMovable {
	
	/**
	 * Moves the system at a speed to a given direction along the y-axis.
	 * @param speed speed [-1...1]
	 */
	void moveY(double speed);
	/**
	 * Moves the system at a speed to a given direction along the y-axis.
	 * @param speed speed [0...1]
	 * @param direction right - true, left - false
	 */
	default void moveY(double speed, boolean direction){
		moveY(direction? speed : -speed);
	}
	/**
	 * Moves the system at a speed forwards.
	 * 
	 * <p>Default implementation calls {@link #moveY(double, boolean)} with the given speed
	 * and true for direction.
	 * 
	 * @param speed speed [0...1]
	 */
	default void forward(double speed){
		moveY(speed, true);
	}
	/**
	 * Moves the system at a speed backwards.
	 * <p>Default implementation calls {@link #moveY(double, boolean)} with the given speed
	 * and false for direction.
	 * @param speed speed [0...1]
	 */
	default void backward(double speed){
		moveY(speed, false);
	}
	/**
	 * Gets the {@link Subsystem} object for this system to use with actions.
	 * @return the system object
	 */
	Subsystem getSystem();
	/**
	 * Stops the system
	 */
	void stop();
}
