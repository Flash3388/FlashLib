package edu.flash3388.flashlib.robot.sbc.bulldog;

import io.silverspoon.bulldog.core.pin.Pin;

public class TalonSrx extends PwmSpeedController{

	public TalonSrx(Pin port) {
		super(port);
		
		setFrequency(333);
		setBounds(2.004, 1.52, 1.50, 1.48, .997);
		
		setSpeed(0.0);
	}

}
