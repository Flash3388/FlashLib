package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.robot.io.PWM;

/**
 * Provides safety features for PWM controlled devices. Extends {@link PWMActuator},
 * adding implementation of {@link SafeMotor}. PWM devices should extend this
 * devices to be able to use the safety feature from FlashLib.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class SafePWMMotor extends PWMActuator implements SafeMotor{

	private MotorSafetyHelper helper;

	/**
	 * Creates a new safe PWM device for a given PWM port.
	 * 
	 * @param port PWM port
	 * @param bounds pwm bounds
	 */
	public SafePWMMotor(PWM port, PWMBounds bounds) {
		super(port, bounds);
		helper = new MotorSafetyHelper(this);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks with the safety helper to see if this actuator is enabled. If it is, speed
	 * is set and the safety helper is notified.
	 */
	@Override
	public void setSpeed(double speed){
		if(helper.isMotorEnabled()){
			super.setSpeed(speed);
			helper.feed();
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks with the safety helper to see if this actuator is enabled. If it is, position
	 * is set and the safety helper is notified.
	 */
	@Override
	public void setPosition(double pos){
		if(helper.isMotorEnabled()){
			super.setPosition(pos);
			helper.feed();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setExpiration(int timeout) {
		helper.setExpiration(timeout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getExpiration() {
		return helper.getExpiration();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAlive() {
		return helper.isAlive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSafetyEnabled(boolean enabled) {
		helper.setSafetyEnabled(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSafetyEnabled() {
		return helper.isSafetyEnabled();
	}
}
