package com.flash3388.flashlib.robot.nint;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.control.Stoppable;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

/**
 * Describes a component capable of moving along a single axis.
 *
 * @since FlashLib 3.0.0
 */
public interface Movable extends Stoppable, Requirement {

    /**
     * Moves along the axis at a given speed.
     *
     * @param speed supplier of speed of motion [-1..1] describing
     *              percentage of power and direction.
     * @return action to perform motion.
     */
    Action moveAt(DoubleSupplier speed);

    /**
     * Moves along the axis at a given speed.
     *
     * @param speed speed of motion [-1..1] describing
     *              percentage of power and direction.
     * @return action to perform motion.
     */
    default Action moveAt(double speed) {
        return moveAt(Suppliers.of(speed));
    }

    /**
     * Moves along the axis at a given speed and direction.
     *
     * @param speed absolute speed of motion [0...1] describing
     *              percentage of power.
     * @param direction direction of motion.
     * @return action to perform motion.
     */
    default Action moveAt(double speed, Direction direction) {
        return moveAt(Math.abs(speed) * direction.sign());
    }

    /**
     * Moves along the axis forward at a given speed.
     *
     * @param speed supplier absolute speed of motion [0...1] describing
     *              percentage of power.
     * @return action to perform motion.
     */
    default Action forwardAt(DoubleSupplier speed) {
        return moveAt(()-> Math.abs(speed.getAsDouble()));
    }

    /**
     * Moves along the axis forward at a given speed.
     *
     * @param speed absolute speed of motion [0...1] describing
     *              percentage of power.
     * @return action to perform motion.
     */
    default Action forwardAt(double speed) {
        return moveAt(speed, Direction.FORWARD);
    }

    /**
     * Moves along the axis backward at a given speed.
     *
     * @param speed supplier absolute speed of motion [0...1] describing
     *              percentage of power.
     * @return action to perform motion.
     */
    default Action backwardAt(DoubleSupplier speed) {
        return moveAt(()-> -Math.abs(speed.getAsDouble()));
    }

    /**
     * Moves along the axis backward at a given speed.
     *
     * @param speed absolute speed of motion [0...1] describing
     *              percentage of power.
     * @return action to perform motion.
     */
    default Action backwardAt(double speed) {
        return moveAt(speed, Direction.BACKWARD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void stop();
}
