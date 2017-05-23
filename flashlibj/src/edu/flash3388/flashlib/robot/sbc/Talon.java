package edu.flash3388.flashlib.robot.sbc;

import io.silverspoon.bulldog.core.pin.Pin;

public class Talon extends PwmSpeedController{

	public Talon(Pin port) {
		super(port);
		
		setFrequency(333);
		setBounds(2.037, 1.539, 1.513, 1.487, .989);
		
		setSpeed(0.0);
	}

}
