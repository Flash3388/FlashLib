package edu.flash3388.flashlib.robot.systems;

/**
 * Interface for object with the capability to rotate.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Rotatable {
	
	/**
	 * Rotates the system at a speed to a given direction.
	 * @param speed speed [-1...1]
	 */
	void rotate(double speed);
	/**
	 * Rotates the system at a speed to a given direction.
	 * @param speed speed [0...1]
	 * @param direction right - true, left - false
	 */
	default void rotate(double speed, boolean direction){
		rotate(direction? speed : -speed);
	}
	/**
	 * Rotates the system at a speed to the right.
	 * <p>Default implementation calls {@link #rotate(double, boolean)} with the given speed
	 * and true for direction.
	 * @param speed speed [0...1]
	 */
	default void rotateRight(double speed){
		rotate(speed, true);
	}
	/**
	 * Rotates the system at a speed to the left.
	 * <p>Default implementation calls {@link #rotate(double, boolean)} with the given speed
	 * and true for direction.
	 * @param speed speed [0...1]
	 */
	default void rotateLeft(double speed){
		rotate(speed, false);
	}
	/**
	 * Stops the system.
	 */
	void stop();
}
