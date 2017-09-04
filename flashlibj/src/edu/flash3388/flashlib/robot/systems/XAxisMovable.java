package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Subsystem;

/**
 * Interface for object with the capability to move along the x-axis.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface XAxisMovable {
	/**
	 * Moves the system at a speed to a given direction along the x-axis.
	 * @param speed speed [0...1]
	 * @param direction right - true, left - false
	 */
	void moveX(double speed, boolean direction);
	/**
	 * Moves the system at a speed to the right.
	 * @param speed speed [0...1]
	 */
	void right(double speed);
	/**
	 * Moves the system at a speed to the left.
	 * @param speed speed [0...1]
	 */
	void left(double speed);
	/**
	 * Stops the system.
	 */
	void stop();
	/**
	 * Gets the {@link Subsystem} object for this system to use with actions.
	 * @return the system object
	 */
	Subsystem getSystem();
}
