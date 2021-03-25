package com.flash3388.flashlib.robot.motion.supervised;

import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.control.Stoppable;
import com.flash3388.flashlib.scheduling.Requirement;

public interface SupervisedMovable extends Stoppable, Supervisable, Requirement {

    default void supervisedMove(double speed) {
        if (isInBounds(speed))
            move(speed);
        else
            stop();
    }

    /**
     * Moves along the axis at a given speed.
     *
     * @param speed speed of motion [-1..1] describing
     *              percentage of power and direction.
     */
    void move(double speed);

    /**
     * Moves along the axis at a given speed and direction.
     *
     * @param speed absolute speed of motion [0...1] describing
     *              percentage of power.
     * @param direction direction of motion.
     */
    default void move(double speed, Direction direction) {
        move(Math.abs(speed) * direction.sign());
    }

    /**
     * Moves along the axis forward at a given speed.
     *
     * @param speed absolute speed of motion [0...1] describing
     *              percentage of power.
     */
    default void forward(double speed) {
        move(speed, Direction.FORWARD);
    }

    /**
     * Moves along the axis backward at a given speed.
     *
     * @param speed absolute speed of motion [0...1] describing
     *              percentage of power.
     */
    default void backward(double speed) {
        move(speed, Direction.BACKWARD);
    }
}
