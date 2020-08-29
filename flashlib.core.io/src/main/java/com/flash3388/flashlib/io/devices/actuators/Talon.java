package com.flash3388.flashlib.io.devices.actuators;

import com.flash3388.flashlib.io.Pwm;

/**
 * Control class for the Talon and Talon SR speed controllers from Cross the Road Electronics. This
 * controller uses a Pwm port for control and is those a {@link PwmSpeedController}.
 * <p>
 * Note that this speed controller has been discontinued by CTRE.
 *
 * @since FlashLib 1.2.0
 */
public class Talon extends PwmSpeedController {

	/**
	 * Creates a new Talon or Talon SR control class for a given Pwm channel.
	 * 
	 * @param port Pwm port object.
	 */
	public Talon(Pwm port) {
		super(port,
                new PwmBounds(2.037, 1.539, 1.513,
                        1.487, 0.989, false),
                333.0);
	}
}
