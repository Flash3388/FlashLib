package edu.flash3388.flashlib.robot.systems;

/**
 * Interface for holonomic drive systems, i.e. Mecanum and Omni. Extends {@link DriveSystem}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface HolonomicDriveSystem extends DriveSystem{
	/**
	 * Moves the drive system using a given Cartesian vector.
	 * 
	 * @param x the x-coordinate of the vector
	 * @param y the y-coordinate of the vector
	 * @param rotation the degree of rotation
	 */
	void holonomicCartesian(double x, double y, double rotation);
	/**
	 * Moves the drive system using a given Polar vector.
	 * 
	 * @param magnitude the magnitude of the vector
	 * @param direction the angle of the vector from the y-axis
	 * @param rotation the degree of rotation
	 */
	void holonomicPolar(double magnitude, double direction, double rotation);
}
