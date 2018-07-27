package edu.flash3388.flashlib.robot.systems;

/**
 * Interface for object with the capability to move along the x-axis.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface XAxisMovable {
	
	/**
	 * Moves the system at a speed to a given direction along the x-axis.
	 * @param speed speed [-1...1]
	 */
	void moveX(double speed);

	/**
	 * Moves the system at a speed to a given direction along the x-axis.
	 * @param speed speed [0...1]
	 * @param direction right - true, left - false
	 */
	default void moveX(double speed, boolean direction){
		moveX(direction? speed : -speed);
	}

	/**
	 * Moves the system at a speed to the right.
	 * <p>Default implementation calls {@link #moveX(double, boolean)} with the given speed
	 * and true for direction.
	 * @param speed speed [0...1]
	 */
	default void right(double speed){
		moveX(speed, true);
	}

	/**
	 * Moves the system at a speed to the left.
	 * <p>Default implementation calls {@link #moveX(double, boolean)} with the given speed
	 * and true for direction.
	 * @param speed speed [0...1]
	 */
	default void left(double speed){
		moveX(speed, false);
	}

	/**
	 * Stops the system.
	 */
	void stop();
}
