package edu.flash3388.flashlib.robot.devices;

public interface FlashSpeedController{
	void set(double speed);
	void set(double speed, int direction);
	void set(double speed, boolean direction);
	
	void stop();
	
	double get();
	boolean isInverted();
	void setInverted(boolean inverted);
}
