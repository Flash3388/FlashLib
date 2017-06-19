package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.System;

/**
 * Interface for object with the capability to rotate.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Rotatable {
	/**
	 * Rotates the system at a speed to a given direction.
	 * @param speed speed [0...1]
	 * @param direction right - 1, left - -1
	 */
	void rotate(double speed, boolean direction);
	/**
	 * Rotates the system at a speed to the right.
	 * @param speed speed [0...1]
	 */
	void rotateRight(double speed);
	/**
	 * Rotates the system at a speed to the left.
	 * @param speed speed [0...1]
	 */
	void rotateLeft(double speed);
	/**
	 * Stops the system.
	 */
	void stop();
	/**
	 * Gets the {@link System} object for this system to use with actions.
	 * @return the system object
	 */
	System getSystem();
}
