package edu.flash3388.flashlib.robot.devices;

public class TalonSRX extends PWMSpeedController{

	public TalonSRX(PWM port) {
		super(port);
		
		setFrequency(333.0);
		setBounds(2.004, 1.52, 1.50, 1.48, 0.997);
		
		setSpeed(0.0);
	}
}
