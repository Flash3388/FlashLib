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
	 * @param speed speed [0...1]
	 * @param direction forward - 1, backward - -1
	 */
	void driveY(double speed, boolean direction);
	/**
	 * Moves the system at a speed forwards.
	 * @param speed speed [0...1]
	 */
	void forward(double speed);
	/**
	 * Moves the system at a speed backwards.
	 * @param speed speed [0...1]
	 */
	void backward(double speed);
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
