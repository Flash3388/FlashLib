package edu.flash3388.flashlib.robot.sbc.devices;

import edu.flash3388.flashlib.hal.PWM;

public class TalonSRX extends PWMSpeedController{

	public TalonSRX(int port){
		this(new PWM(port));
	}
	public TalonSRX(PWM port) {
		super(port);
		
		setFrequency(333);
		setBounds(2.004, 1.52, 1.50, 1.48, .997);
		
		setSpeed(0.0);
	}
}
