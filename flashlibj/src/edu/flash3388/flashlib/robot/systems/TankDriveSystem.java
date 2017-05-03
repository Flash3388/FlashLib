package edu.flash3388.flashlib.robot.systems;

public interface TankDriveSystem extends DriveSystem{
	void forward(double right, double left);
	void backward(double right, double left);
}
