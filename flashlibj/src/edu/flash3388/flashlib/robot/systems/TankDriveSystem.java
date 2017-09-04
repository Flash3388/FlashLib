package edu.flash3388.flashlib.robot.systems;

/**
 * Interface for tank drive systems. Extends {@link RobotDriveSystem}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface TankDriveSystem extends RobotDriveSystem{
	void arcadeDrive(double moveValue, double rotateValue);
}
