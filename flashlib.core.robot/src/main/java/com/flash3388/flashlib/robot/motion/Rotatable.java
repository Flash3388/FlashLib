package com.flash3388.flashlib.robot.motion;

import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.control.Stoppable;
import com.flash3388.flashlib.scheduling.Requirement;

/**
 * Interface for object with the capability to rotate.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Rotatable extends Stoppable, Requirement {
	
	/**
	 * Rotates the system at a given speed.
     *
	 * @param speed speed of motion [-1..1] describing percentage of power and direction.
	 */
	void rotate(double speed);

	/**
	 * Rotates the system at a given speed.
     *
	 * @param speed speed of motion [0..1] describing percentage of power.
	 * @param direction right - forward, left - backward
	 */
	default void rotate(double speed, Direction direction){
		rotate(speed * direction.sign());
	}

	/**
	 * Rotates the system at a speed to the right.
     *
	 * <p>Default implementation calls {@link #rotate(double, Direction)} with the given speed
     * and {@link Direction#FORWARD} for direction.
	 * @param speed speed of motion [0..1] describing percentage of power.
	 */
	default void rotateRight(double speed){
		rotate(speed, Direction.FORWARD);
	}

	/**
	 * Rotates the system at a speed to the left.
     *
	 * <p>Default implementation calls {@link #rotate(double, Direction)} with the given speed
	 * and {@link Direction#BACKWARD} for direction.
	 * @param speed speed speed of motion [0..1] describing percentage of power.
	 */
	default void rotateLeft(double speed){
		rotate(speed, Direction.BACKWARD);
	}
}
