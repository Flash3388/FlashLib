package edu.flash3388.flashlib.robot.systems;

/**
 * Interface for tank drive systems. Extends {@link RobotDriveSystem}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface TankDriveSystem extends RobotDriveSystem{
	
	 /** Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move 
	 * each side separately.
	 * 
	 * @param right The speed value of the right side of motors 1 to -1.
	 * @param left The speed value of the left side of motors 1 to -1.
	 */
	void tankDrive(double right, double left);
	
	/**
	 * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values 
	 * to move the tank drive. The move value is responsible for moving the robot forward and backward while the 
	 * rotate value is responsible for the robot rotation. 
	 * <p>
	 * The default implementation calculates appropriate values to move each side of the drive system (right and left)
	 * and passes those values to {@link #tankDrive(double, double)}.
	 * 
	 * @param moveValue The value to move forward or backward 1 to -1.
	 * @param rotateValue The value to rotate right or left 1 to -1.
	 * 
	 * @see FlashDrive#calculate_arcadeDrive(double, double)
	 */
	default void arcadeDrive(double moveValue, double rotateValue){
		double[] values = FlashDrive.calculate_arcadeDrive(moveValue, rotateValue);
		tankDrive(values[0], values[1]);
	}
}
