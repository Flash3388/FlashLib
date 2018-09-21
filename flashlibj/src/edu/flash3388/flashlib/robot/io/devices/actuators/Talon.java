package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.robot.io.PWM;

/**
 * Control class for the Talon and Talon SR speed controllers from Cross the Road Electronics. This
 * controller uses a PWM port for control and is those a {@link PWMSpeedController}.
 * <p>
 * Note that this speed controller has been discontinued by CTRE.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class Talon extends PWMSpeedController {

	/**
	 * Creates a new Talon or Talon SR control class for a given PWM channel.
	 * 
	 * @param port PWM port object.
	 */
	public Talon(PWM port) {
		super(port,
                new PWMBounds(2.037, 1.539, 1.513, 1.487, 0.989, false),
                333.0);
	}
}
