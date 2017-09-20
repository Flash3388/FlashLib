package edu.flash3388.flashlib.robot.devices;

public interface PWM {

	void setDuty(double duty);
	void setRaw(int raw);
	
	double getDuty();
	int getRaw();
	
	void setFrequency(double frequency);
	double getFrequency();
}
