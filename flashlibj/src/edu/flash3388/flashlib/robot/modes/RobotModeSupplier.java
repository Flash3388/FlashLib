package edu.flash3388.flashlib.robot.modes;

@FunctionalInterface
public interface RobotModeSupplier {
	
	RobotMode getMode();
}
