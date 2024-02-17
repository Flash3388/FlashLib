package com.flash3388.flashlib.robot.systems;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

/**
 * Interface for object with the capability to rotate.
 *
 * @since FlashLib 3.2.0
 */
public interface Rotatable extends Stoppable, Requirement {

    /**
     * Rotates the system at a given speed.
     *
     * @param speed supplier of speed of motion [-1..1] describing percentage of power and direction.
     * @return action to perform motion.
     */
    Action rotate(DoubleSupplier speed);

    /**
     * Rotates the system at a given speed.
     *
     * @param speed speed of motion [-1..1] describing percentage of power and direction.
     * @return action to perform motion.
     */
    default Action rotate(double speed) {
        return rotate(Suppliers.of(speed));
    }

    /**
     * Rotates the system at a given speed.
     *
     * @param speed speed of motion [0..1] describing percentage of power.
     * @param direction right - forward, left - backward
     * @return action to perform motion.
     */
    default Action rotate(double speed, Direction direction){
        return rotate(Math.abs(speed) * direction.sign());
    }

    /**
     * Rotates the system at a speed to the right.
     *
     * <p>Default implementation calls {@link #rotate(double, Direction)} with the given speed
     * and {@link Direction#FORWARD} for direction.
     * @param speed supplier of speed of motion [0..1] describing percentage of power.
     * @return action to perform motion.
     */
    default Action rotateRight(DoubleSupplier speed){
        return rotate(()-> Math.abs(speed.getAsDouble()));
    }

    /**
     * Rotates the system at a speed to the right.
     *
     * <p>Default implementation calls {@link #rotate(double, Direction)} with the given speed
     * and {@link Direction#FORWARD} for direction.
     * @param speed speed of motion [0..1] describing percentage of power.
     * @return action to perform motion.
     */
    default Action rotateRight(double speed){
        return rotate(speed, Direction.FORWARD);
    }

    /**
     * Rotates the system at a speed to the left.
     *
     * <p>Default implementation calls {@link #rotate(double, Direction)} with the given speed
     * and {@link Direction#BACKWARD} for direction.
     * @param speed suppliers speed of motion [0..1] describing percentage of power.
     * @return action to perform motion.
     */
    default Action rotateLeft(DoubleSupplier speed){
        return rotate(()-> -Math.abs(speed.getAsDouble()));
    }

    /**
     * Rotates the system at a speed to the left.
     *
     * <p>Default implementation calls {@link #rotate(double, Direction)} with the given speed
     * and {@link Direction#BACKWARD} for direction.
     * @param speed speed speed of motion [0..1] describing percentage of power.
     * @return action to perform motion.
     */
    default Action rotateLeft(double speed){
        return rotate(speed, Direction.BACKWARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void stop();
}
