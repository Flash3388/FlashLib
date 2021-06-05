package com.flash3388.flashlib.robot.systems2;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.jmath.vectors.Vector2;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public interface Movable2d extends Movable {

    /**
     * Moves along both axes at given speeds.
     *
     * @param speed supplier of vector describing speed of motion, where
     *              each axis [-1...1] describes percentage of power
     *              and direction.
     * @return action to perform motion.
     */
    Action move2d(Supplier<Vector2> speed);

    /**
     * Moves along both axes at given speeds.
     *
     * @param speed vector describing speed of motion, where
     *              each axis [-1...1] describes percentage of power
     *              and direction.
     * @return action to perform motion.
     */
    default Action move2d(Vector2 speed) {
        return move2d(Suppliers.of(speed));
    }

    /**
     * Moves along both axes at given speeds.
     *
     * @param y supplier of speed on y axis to move motors [-1, 1]
     * @param x supplier of speed on x axis to move motors [-1, 1]
     * @return action to perform motion.
     */
    default Action move2d(DoubleSupplier y, DoubleSupplier x) {
        return move2d(()-> new Vector2(x.getAsDouble(), y.getAsDouble()));
    }

    /**
     * Moves along both axes at given speeds.
     *
     * @param y speed on y axis to move motors [-1, 1]
     * @param x speed on x axis to move motors [-1, 1]
     * @return action to perform motion.
     */
    default Action move2d(double y, double x) {
        return move2d(new Vector2(x, y));
    }

    /**
     * Moves along both axes at given speeds.
     *
     * @param magnitude supplier of speed magnitude to move motors [-1, 1]
     * @param orientation supplier of speed orientation to move motors [0, 360]
     * @return action to perform motion.
     */
    default Action move2dPolar(DoubleSupplier magnitude, DoubleSupplier orientation) {
        return move2d(()-> Vector2.polar(magnitude.getAsDouble(), orientation.getAsDouble()));
    }

    /**
     * Moves along both axes at given speeds.
     *
     * @param magnitude speed magnitude to move motors [-1, 1]
     * @param orientation speed orientation to move motors [0, 360]
     * @return action to perform motion.
     */
    default Action move2dPolar(double magnitude, double orientation) {
        return move2d(Vector2.polar(magnitude, orientation));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void stop();
}
