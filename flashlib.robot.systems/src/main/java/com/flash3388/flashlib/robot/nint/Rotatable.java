package com.flash3388.flashlib.robot.nint;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.control.Stoppable;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

/**
 * Interface for object with the capability to rotate.
 *
 * @since FlashLib 3.0.0
 */
public interface Rotatable extends Stoppable, Requirement {

    /**
     * Rotates the system at a given speed.
     *
     * @param speed supplier of speed of motion [-1..1] describing percentage of power and direction.
     * @return action to perform motion.
     */
    Action rotateAt(DoubleSupplier speed);

	/**
	 * Rotates the system at a given speed.
     *
	 * @param speed speed of motion [-1..1] describing percentage of power and direction.
     * @return action to perform motion.
	 */
	default Action rotateAt(double speed) {
	    return rotateAt(Suppliers.of(speed));
    }

	/**
	 * Rotates the system at a given speed.
     *
	 * @param speed speed of motion [0..1] describing percentage of power.
	 * @param direction right - forward, left - backward
     * @return action to perform motion.
	 */
	default Action rotateAt(double speed, Direction direction){
        return rotateAt(Math.abs(speed) * direction.sign());
	}

    /**
     * Rotates the system at a speed to the right.
     *
     * <p>Default implementation calls {@link #rotateAt(double, Direction)} with the given speed
     * and {@link Direction#FORWARD} for direction.
     * @param speed supplier of speed of motion [0..1] describing percentage of power.
     * @return action to perform motion.
     */
    default Action rotateRightAt(DoubleSupplier speed){
        return rotateAt(()-> Math.abs(speed.getAsDouble()));
    }

	/**
	 * Rotates the system at a speed to the right.
     *
	 * <p>Default implementation calls {@link #rotateAt(double, Direction)} with the given speed
     * and {@link Direction#FORWARD} for direction.
	 * @param speed speed of motion [0..1] describing percentage of power.
     * @return action to perform motion.
	 */
	default Action rotateRightAt(double speed){
        return rotateAt(speed, Direction.FORWARD);
	}

    /**
     * Rotates the system at a speed to the left.
     *
     * <p>Default implementation calls {@link #rotateAt(double, Direction)} with the given speed
     * and {@link Direction#BACKWARD} for direction.
     * @param speed suppliers speed speed of motion [0..1] describing percentage of power.
     * @return action to perform motion.
     */
    default Action rotateLeftAt(DoubleSupplier speed){
        return rotateAt(()-> -Math.abs(speed.getAsDouble()));
    }

	/**
	 * Rotates the system at a speed to the left.
     *
	 * <p>Default implementation calls {@link #rotateAt(double, Direction)} with the given speed
	 * and {@link Direction#BACKWARD} for direction.
	 * @param speed speed speed of motion [0..1] describing percentage of power.
     * @return action to perform motion.
	 */
	default Action rotateLeftAt(double speed){
        return rotateAt(speed, Direction.BACKWARD);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    void stop();
}
