package com.flash3388.flashlib.robot.motion;

import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.scheduling.Requirement;
import com.jmath.vectors.Vector2;

/**
 * Describes a component capable of moving along 2 axes.
 *
 * @since FlashLib 2.0.0
 */
public interface Movable2d extends Movable, Requirement {

    /**
     * Moves along both axes at given speeds.
     *
     * @param motionVector vector describing speed of motion, where
     *                     each axis [-1...1] describes percentage of power
     *                     and direction.
     */
    void move(Vector2 motionVector);

    /**
     * Moves along Y axis at a given speed.
     *
     * @param speed speed of motion [-1..1] describing percentage of
     *              power and direction.
     */
    @Override
    default void move(double speed) {
        move(Vector2.polar(speed, 0.0));
    }

    /**
     * Moves along Y axis at a given speed.
     *
     * @param speed speed of motion [0..1] describing percentage of
     *              power.
     * @param direction direction motion.
     */
    @Override
    default void move(double speed, Direction direction) {
        move(speed * direction.sign());
    }

    /**
     * Moves forward along Y axis at a given speed.
     *
     * @param speed speed of motion [0..1] describing percentage of
     *              power.
     */
    @Override
    default void forward(double speed) {
        move(speed, Direction.FORWARD);
    }

    /**
     * Moves backward along Y axis at a given speed.
     *
     * @param speed speed of motion [0..1] describing percentage of
     *              power.
     */
    @Override
    default void backward(double speed) {
        move(speed, Direction.BACKWARD);
    }
}
