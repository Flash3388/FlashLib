package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.math.Mathf;

/**
 * Interface for holonomic drive systems, i.e. Mecanum and Omni. Extends {@link RobotDriveSystem}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface HolonomicDriveSystem extends RobotDriveSystem, XAxisMovable{
	
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
	 * <p>
	 * The default implementation converts the given vector to a cartesian form and calls 
	 * {@link #holonomicCartesian(double, double, double)}.
	 * 
	 * @param magnitude the magnitude of the vector
	 * @param direction the angle of the vector from the y-axis
	 * @param rotation the degree of rotation
	 */
	default void holonomicPolar(double magnitude, double direction, double rotation){
		holonomicCartesian(Mathf.vecX(magnitude, direction), Mathf.vecY(magnitude, direction), rotation);
	}
}
