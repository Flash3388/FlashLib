package edu.flash3388.flashlib.robot.systems.drive;

import edu.flash3388.flashlib.math.Vector2;
import edu.flash3388.flashlib.robot.systems.XAxisMovableInterface;

/**
 * Interface for holonomic drive systems, i.e. Mecanum and Omni. Extends {@link DriveInterface}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface HolonomicDriveInterface extends DriveInterface, XAxisMovableInterface {
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * A default implementation is provided. It calls {@link #holonomicCartesian(double, double, double)}
	 * and passes it the given speed for y while passing 0.0 for the rest of the parameters.
	 * 
	 */
	@Override
	default void moveY(double speed) {
		holonomicCartesian(speed, 0.0, 0.0);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * A default implementation is provided. It calls {@link #holonomicCartesian(double, double, double)}
	 * and passes it the given speed for the x parameters while passing 0.0 for the rest of the parameters.
	 * 
	 */
	@Override
	default void moveX(double speed) {
		holonomicCartesian(0.0, speed, 0.0);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * A default implementation is provided. It calls {@link #holonomicCartesian(double, double, double)}
	 * and passes it the given for the rotation parameter while passing 0.0 for the rest of the parameters.
	 */
	@Override
	default void rotate(double speed) {
		holonomicCartesian(0.0, 0.0, speed);
	}
	
	/**
	 * Moves the drive system using a given Cartesian vector.
	 * 
	 * @param y the y-coordinate of the vector
	 * @param x the x-coordinate of the vector
	 * @param rotation the degree of rotation
	 */
	void holonomicCartesian(double y, double x, double rotation);
	
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
        Vector2 vector = Vector2.polar(magnitude, direction);
		holonomicCartesian(vector.getY(), vector.getX(), rotation);
	}
	
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
	 * the X value to move the wheels in the front and back.
	 * <p>
	 * The default implementation call {@link #holonomicCartesian(double, double, double)} and
	 * passes it the y and x values. The rotation value is 0.0.
	 * 
	 * @param y the y-coordinate of the vector
	 * @param x the x-coordinate of the vector
	 */
	default void omniDrive(double y, double x){
		holonomicCartesian(y, x, 0.0);
	}
}
