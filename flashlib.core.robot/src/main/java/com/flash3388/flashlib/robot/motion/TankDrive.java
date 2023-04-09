package com.flash3388.flashlib.robot.motion;

/**
 * Interface for tank drive systems. Extends {@link Drive}.
 *
 * @since FlashLib 1.0.0
 */
public interface TankDrive extends Drive, Movable {

    /**
     * {@inheritDoc}
     * <p>
     * A default implementation is provided. It calls {@link #tankDrive(double, double)} and
     * passes it the speed parameter for both sides.
     */
    @Override
    default void move(double speed) {
        tankDrive(speed, speed);
    }

	/**
	 * {@inheritDoc}
	 * <p>
	 * A default implementation is provided. It calls {@link #tankDrive(double, double)} and
	 * passes it the speed parameter for both sides. The left side receives the given speed value
	 * while right side receives an inverted speed value to cause rotation.
	 */
	@Override
	default void rotate(double speed) {
		tankDrive(-speed, speed);
	}

    /**
     * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move
	 * each side separately.
	 * 
	 * @param right The speed value of the right side of motors 1 to -1.
	 * @param left The speed value of the left side of motors 1 to -1.
	 */
	void tankDrive(double right, double left);

    /**
     * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move
     * each side separately.
     *
     * @param driveSpeed speeds for the motors.
     */
    default void tankDrive(TankDriveSpeed driveSpeed) {
        tankDrive(driveSpeed.getRight(), driveSpeed.getLeft());
    }

	/**
	 * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values 
	 * to move the tank drive. The move value is responsible for moving the robot forward and backward while the 
	 * rotate value is responsible for the robot rotation.
	 * 
	 * @param moveValue The value to move forward or backward 1 to -1.
	 * @param rotateValue The value to rotate right or left 1 to -1.
	 */
	void arcadeDrive(double moveValue, double rotateValue);

    /**
     * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values
     * to move the tank drive. The move value is responsible for moving the robot forward and backward while the
     * rotate value is responsible for the robot rotation.
     *
     * @param driveSpeed speeds for motors.
     */
	default void arcadeDrive(ArcadeDriveSpeed driveSpeed) {
	    arcadeDrive(driveSpeed.getMove(), driveSpeed.getRotate());
    }
}
