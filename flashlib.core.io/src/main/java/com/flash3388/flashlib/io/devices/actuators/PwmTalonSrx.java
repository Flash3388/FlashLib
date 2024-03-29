package com.flash3388.flashlib.io.devices.actuators;

import com.flash3388.flashlib.io.Pwm;
import com.flash3388.flashlib.io.devices.DeviceConstructor;
import com.flash3388.flashlib.io.devices.NamedArg;

/**
 * Control class for the Talon SRX speed controller from Cross the Road Electronics. This
 * controller uses a Pwm port for control and is those a {@link PwmSpeedController}.
 *
 * @since FlashLib 1.2.0
 */
public class PwmTalonSrx extends PwmSpeedController {

	/**
	 * Creates a new Talon SRX control class for a given Pwm channel.
	 * 
	 * @param port Pwm port object.
	 */
	@DeviceConstructor
	public PwmTalonSrx(
			@NamedArg("port") Pwm port
	) {
		super(
		        port,
                new PwmBounds(2.004, 1.52, 1.50, 1.48, 0.997, false),
                333.0);
	}
}
