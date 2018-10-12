package edu.flash3388.flashlib.robot.motion;

/**
 * Interface for object with the capability to rotate.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Rotatable extends Stoppable {
	
	/**
	 * Rotates the system at a speed to a given direction.
	 * @param speed speed [-1...1]
	 */
	void rotate(double speed);
	/**
	 * Rotates the system at a speed to a given direction.
	 * @param speed speed [0...1]
	 * @param direction right - forward, left - backward
	 */
	default void rotate(double speed, Direction direction){
		rotate(speed * direction.sign());
	}
	/**
	 * Rotates the system at a speed to the right.
	 * <p>Default implementation calls {@link #rotate(double, Direction)} with the given speed
     * 	 * and {@link Direction#FORWARD} for direction.
	 * @param speed speed [0...1]
	 */
	default void rotateRight(double speed){
		rotate(speed, Direction.FORWARD);
	}
	/**
	 * Rotates the system at a speed to the left.
	 * <p>Default implementation calls {@link #rotate(double, Direction)} with the given speed
	 * and {@link Direction#BACKWARD} for direction.
	 * @param speed speed [0...1]
	 */
	default void rotateLeft(double speed){
		rotate(speed, Direction.BACKWARD);
	}
}
