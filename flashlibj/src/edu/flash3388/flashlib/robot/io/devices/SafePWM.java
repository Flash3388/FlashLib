package edu.flash3388.flashlib.robot.io.devices;

import edu.flash3388.flashlib.robot.io.IOFactory;
import edu.flash3388.flashlib.robot.io.PWM;
import edu.flash3388.flashlib.robot.io.devices.actuators.MotorSafetyHelper;
import edu.flash3388.flashlib.robot.io.devices.actuators.SafeMotor;

/**
 * Provides safety features for PWM controlled devices. Extends {@link PWMDevice},
 * adding implementation of {@link SafeMotor}. PWM devices should extend this
 * devices to be able to use the safety feature from FlashLib.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class SafePWM extends PWMDevice implements SafeMotor{

	private MotorSafetyHelper helper;
	
	/**
	 * Creates a new PWM device for a given PWM port number. The port object
	 * is created by calling {@link IOFactory#createPWMPort(int)}.
	 * 
	 * @param port PWM port channel
	 */
	public SafePWM(int port) {
		super(port);
		helper = new MotorSafetyHelper(this);
	}
	/**
	 * Creates a new safe PWM device for a given PWM port.
	 * 
	 * @param port PWM port
	 */
	public SafePWM(PWM port) {
		super(port);
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
