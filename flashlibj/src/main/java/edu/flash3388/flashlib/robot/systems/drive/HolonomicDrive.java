package edu.flash3388.flashlib.robot.systems.drive;

import com.jmath.vectors.Vector2;
import edu.flash3388.flashlib.robot.motion.Movable2d;

/**
 * Interface for holonomic drive systems, i.e. Mecanum and Omni. Extends {@link Drive}.
 * 
 * @author Tom Tzook
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
}
