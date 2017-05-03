package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.robot.Direction;
import edu.flash3388.flashlib.robot.systems.ModableMotor;

public interface FlashSpeedController extends ModableMotor{
	void set(double speed);
	void set(double speed, int direction);
	
	void stop();
	
	double get();
	boolean isInverted();
	void setInverted(boolean inverted);
}
