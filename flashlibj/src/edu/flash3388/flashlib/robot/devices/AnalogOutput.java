package edu.flash3388.flashlib.robot.devices;

public interface AnalogOutput {

	void setValue(int value);
	void setVoltage(double voltage);
	
	double getVoltage();
	int getValue();
}
