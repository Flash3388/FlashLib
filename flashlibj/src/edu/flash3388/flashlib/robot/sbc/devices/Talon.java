package edu.flash3388.flashlib.robot.sbc.devices;

import edu.flash3388.flashlib.hal.PWM;

public class Talon extends PWMSpeedController{

	public Talon(int port){
		this(new PWM(port));
	}
	public Talon(PWM port) {
		super(port);
		
		setFrequency(333);
		setBounds(2.037, 1.539, 1.513, 1.487, .989);
		
		setSpeed(0.0);
	}

}
