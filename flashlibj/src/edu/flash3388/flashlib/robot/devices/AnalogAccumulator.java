package edu.flash3388.flashlib.robot.devices;

public interface AnalogAccumulator {

	void setEnabled(boolean enabled);
	
	void reset();
	
	void setCenter(int value);
	
	long getValue();
	int getCount();
}
