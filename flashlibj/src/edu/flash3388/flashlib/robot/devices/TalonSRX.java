package edu.flash3388.flashlib.robot.devices;

public class TalonSRX extends PWMSpeedController{

	public TalonSRX(int port) {
		super(port);
		
		init();
	}
	public TalonSRX(PWM port) {
		super(port);
		
		init();
	}
	
	private void init(){
		setFrequency(333.0);
		setBounds(2.004, 1.52, 1.50, 1.48, 0.997);
		
		setSpeed(0.0);
	}
}
