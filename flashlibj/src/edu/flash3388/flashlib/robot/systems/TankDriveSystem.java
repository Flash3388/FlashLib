package edu.flash3388.flashlib.robot.systems;

public interface TankDriveSystem extends DriveSystem{
	void arcadeDrive(double moveValue, double rotateValue);
}
