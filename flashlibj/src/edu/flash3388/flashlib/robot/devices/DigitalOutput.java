package edu.flash3388.flashlib.robot.devices;

public interface DigitalOutput {

	boolean get();
	
	void set(boolean high);
	void pulse(double length);
}
