package com.flash3388.flashlib.robot.systems.drive;

import com.jmath.vectors.Vector2;

/**
 * Interface for omni drive systems.
 *
 * @since FlashLib 1.2.0
 */
public interface OmniDrive extends HolonomicDrive {

    /**
     * {@inheritDoc}
     */
    @Override
    default void move(Vector2 motionVector) {
        omniDrive(motionVector.y(), motionVector.x());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void rotate(double speed) {
        omniDrive(speed, speed, -speed, -speed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void holonomicCartesian(double y, double x, double rotation);

    /**
     * {@inheritDoc}
     */
    @Override
    void holonomicPolar(double magnitude, double direction, double rotation);

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     * <p>
     * The default implementation call {@link #omniDrive(double, double, double, double)} and
     * passes it the y and x values. The rotation value is 0.0.
     *
     * @param y the y-coordinate of the vector
     * @param x the x-coordinate of the vector
     */
    default void omniDrive(double y, double x) {
        //noinspection SuspiciousNameCombination
        omniDrive(x, y, x, y);
    }

    default void omniDrive(OmniDriveSpeed driveSpeed) {
        omniDrive(driveSpeed.getFront(), driveSpeed.getRight(), driveSpeed.getBack(), driveSpeed.getLeft());
    }

    /**
     * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
     * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
     * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
     * the X value to move the wheels in the front and back.
     *
     * @param front speed for the front side (moving right-left along the x axis)
     * @param right speed for the right side (moving forward-backward along the y axis)
     * @param back speed for the back side (moving right-left along the x axis)
     * @param left speed for the left side (moving forward-backward along the y axis)
     */
    void omniDrive(double front, double right, double back, double left);
}
