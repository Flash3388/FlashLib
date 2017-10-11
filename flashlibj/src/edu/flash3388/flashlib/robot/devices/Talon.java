package edu.flash3388.flashlib.robot.devices;

public class Talon extends PWMSpeedController{

	public Talon(int port) {
		super(port);
		
		init();
	}
	public Talon(PWM port) {
		super(port);
		
		init();
	}

	private void init(){
		setFrequency(333.0);
		setBounds(2.037, 1.539, 1.513, 1.487, 0.989);
		
		setSpeed(0.0);
	}
}
