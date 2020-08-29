package com.flash3388.flashlib.robot.systems.drive;

import com.flash3388.flashlib.robot.motion.Movable2d;
import com.jmath.vectors.Vector2;

/**
 * Interface for holonomic drive systems, i.e. Mecanum and Omni. Extends {@link Drive}.
 *
 * @since FlashLib 1.0.0
 */
public interface HolonomicDrive extends Drive, Movable2d {
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	void move(Vector2 motionVector);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void rotate(double speed);
	
	/**
	 * Moves the drive system using a given Polar vector.
	 * 
	 * @param magnitude the magnitude of the vector
	 * @param direction the angle of the vector from the y-axis
	 * @param rotation the degree of rotation
	 */
	void holonomicPolar(double magnitude, double direction, double rotation);

    /**
     * Moves the drive system using a given Cartesian vector.
     *
     * @param y the y-coordinate of the vector
     * @param x the x-coordinate of the vector
     * @param rotation the degree of rotation
     */
    void holonomicCartesian(double y, double x, double rotation);

    /**
     * Moves the drive system using a 2d motion vector and a rotation value.
     *
     * @param vector 2d motion vector describing motion along y and x axes.
     * @param rotation rotation value [-1...1] describing rotation modifier
     *                 to the motion.
     */
    default void holonomicDrive(Vector2 vector, double rotation) {
        holonomicPolar(vector.magnitude(), vector.angle(), rotation);
    }

    /**
     * Moves the drive system.
     *
     * @param driveSpeed drive speeds.
     */
    default void holonomicDrive(HolonomicDriveSpeed driveSpeed) {
        holonomicDrive(driveSpeed.getDriveVector(), driveSpeed.getRotation());
    }
}
