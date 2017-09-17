package edu.flash3388.flashlib.robot.sbc.devices;

import edu.flash3388.flashlib.hal.PWM;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;

public class PWMSpeedController extends SafePWM implements FlashSpeedController{

	private boolean inverted = false;
	
	public PWMSpeedController(int port) {
		super(port);
	}
	public PWMSpeedController(PWM port) {
		super(port);
	}

	@Override
	public void set(double speed) {
		setSpeed(inverted? -speed : speed);
	}
	@Override
	public void set(double speed, int direction) {
		set(direction >= 0 ? speed : -speed);
	}
	@Override
	public void set(double speed, boolean direction) {
		set(direction ? speed : -speed);
	}

	@Override
	public double get() {
		return getSpeed();
	}

	@Override
	public boolean isInverted() {
		return inverted;
	}
	@Override
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	@Override
	public void stop() {
		set(0);
	}

}
