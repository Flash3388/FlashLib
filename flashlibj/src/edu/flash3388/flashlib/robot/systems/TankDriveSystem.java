package edu.flash3388.flashlib.robot.systems;

/**
 * Interface for tank drive systems. Extends {@link DriveSystem}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface TankDriveSystem extends DriveSystem{
	void arcadeDrive(double moveValue, double rotateValue);
}
