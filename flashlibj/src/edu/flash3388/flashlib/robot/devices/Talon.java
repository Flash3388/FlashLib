package edu.flash3388.flashlib.robot.devices;

public class Talon extends PWMSpeedController{

	public Talon(PWM port) {
		super(port);
		
		setFrequency(333.0);
		setBounds(2.037, 1.539, 1.513, 1.487, 0.989);
		
		setSpeed(0.0);
	}

}
