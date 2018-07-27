package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.robot.io.IOFactory;
import edu.flash3388.flashlib.robot.io.PWM;

/**
 * Control class for the Talon SRX speed controller from Cross the Road Electronics. This
 * controller uses a PWM port for control and is those a {@link PWMSpeedController}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class TalonSRX extends PWMSpeedController{

	/**
	 * Creates a new Talon SRX control class for a given PWM channel.
	 * <p>
	 * The PWM port object is created using {@link IOFactory#createPWMPort(int)}.
	 * 
	 * @param port PWM port number
	 */
	public TalonSRX(int port) {
		super(port);
		
		init();
	}
	/**
	 * Creates a new Talon SRX control class for a given PWM channel.
	 * 
	 * @param port PWM port object.
	 */
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
