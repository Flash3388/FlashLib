package edu.flash3388.flashlib.robot.systems;

public interface HolonomicDriveSystem extends DriveSystem{
	void holonomicCartesian(double x, double y, double rotation);
	void holonomicPolar(double magnitude, double direction, double rotation);
}
