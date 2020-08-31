package com.flash3388.flashlib.robot.systems.drive;

import com.jmath.vectors.Vector2;

/**
 * Interface for mecanum drive systems.
 *
 * @since FlashLib 1.2.0
 */
public interface MecanumDrive extends HolonomicDrive {

    /**
     * {@inheritDoc}
     */
    @Override
    default void move(Vector2 motionVector) {
        holonomicCartesian(motionVector.y(), motionVector.x(), 0.0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void rotate(double speed) {
        holonomicPolar(0.0, 0.0, speed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void holonomicPolar(double magnitude, double direction, double rotation);

    /**
     * {@inheritDoc}
     */
    @Override
    default void holonomicCartesian(double y, double x, double rotation) {
        holonomicDrive(new Vector2(x, y), rotation);
    }

    /**
     * Moves the drive system.
     *
     * @param frontRight speed for front right actuator [-1..1].
     * @param backRight speed for back right actuator [-1..1].
     * @param frontLeft speed for front left actuator [-1..1].
     * @param backLeft speed for back left actuator [-1..1].
     */
    void mecanumDrive(double frontRight, double backRight, double frontLeft, double backLeft);

    /**
     * Moves the drive system.
     *
     * @param driveSpeed speeds for the system actuator.
     */
    default void mecanumDrive(MecanumDriveSpeed driveSpeed) {
        mecanumDrive(driveSpeed.getFrontRight(), driveSpeed.getBackRight(), driveSpeed.getFrontLeft(), driveSpeed.getBackLeft());
    }
}
