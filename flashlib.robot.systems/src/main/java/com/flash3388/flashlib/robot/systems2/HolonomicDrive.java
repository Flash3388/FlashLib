package com.flash3388.flashlib.robot.systems2;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.HolonomicDriveSpeed;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.jmath.vectors.Vector2;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public interface HolonomicDrive extends Movable2d, Rotatable {

    @Override
    default Action move(DoubleSupplier speed) {
        return holonomicDrive(()-> new HolonomicDriveSpeed(new Vector2(0, speed.getAsDouble()), 0));
    }

    @Override
    default Action move(double speed) {
        return holonomicDrive(new HolonomicDriveSpeed(new Vector2(0, speed), 0));
    }

    @Override
    default Action move2d(Supplier<Vector2> speed) {
        return holonomicDrive(()-> new HolonomicDriveSpeed(speed.get(), 0));
    }

    @Override
    default Action move2d(Vector2 speed) {
        return holonomicDrive(new HolonomicDriveSpeed(speed, 0));
    }

    @Override
    default Action rotate(DoubleSupplier speed) {
        return holonomicDrive(()-> new HolonomicDriveSpeed(new Vector2(), speed.getAsDouble()));
    }

    @Override
    default Action rotate(double speed) {
        return holonomicDrive(new HolonomicDriveSpeed(new Vector2(), speed));
    }

    /**
     * Moves the drive system.
     *
     * @param speed supplier of speed for motors [-1, 1].
     * @return action to perform motion.
     */
    Action holonomicDrive(Supplier<HolonomicDriveSpeed> speed);

    /**
     * Moves the drive system.
     *
     * @param speed speed for motors [-1, 1].
     * @return action to perform motion.
     */
    default Action holonomicDrive(HolonomicDriveSpeed speed) {
        return holonomicDrive(Suppliers.of(speed));
    }

    /**
     * Moves the drive system using a 2d motion vector and a rotation value.
     *
     * @param vector supplier of 2d motion vector describing motion along y and x axes.
     * @param rotation supplier of rotation value [-1...1] describing rotation modifier
     *                 to the motion.
     * @return action to perform motion.
     */
    default Action holonomicDrive(Supplier<Vector2> vector, DoubleSupplier rotation) {
        return holonomicDrive(()-> new HolonomicDriveSpeed(vector.get(), rotation.getAsDouble()));
    }

    /**
     * Moves the drive system using a 2d motion vector and a rotation value.
     *
     * @param vector 2d motion vector describing motion along y and x axes.
     * @param rotation rotation value [-1...1] describing rotation modifier
     *                 to the motion.
     * @return action to perform motion.
     */
    default Action holonomicDrive(Vector2 vector, double rotation) {
        return holonomicDrive(new HolonomicDriveSpeed(vector, rotation));
    }

    /**
     * Moves the drive system using a given Cartesian vector.
     *
     * @param y the y-coordinate of the vector
     * @param x the x-coordinate of the vector
     * @param rotation the degree of rotation
     * @return action to perform motion.
     */
    default Action holonomicCartesian(double y, double x, double rotation) {
        return holonomicDrive(new HolonomicDriveSpeed(new Vector2(x, y), rotation));
    }

    /**
     * Moves the drive system using a given Polar vector.
     *
     * @param magnitude the magnitude of the vector
     * @param direction the angle of the vector from the y-axis
     * @param rotation the degree of rotation
     * @return action to perform motion.
     */
    default Action holonomicPolar(double magnitude, double direction, double rotation) {
        return holonomicDrive(new HolonomicDriveSpeed(Vector2.polar(magnitude, direction), rotation));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void stop();
}
