package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.robot.io.PWM;

/**
 * Control class for a PWM-controlled speed controller device. This class integrates 
 * {@link PWMActuator} and {@link SpeedController} into one. For safety, this class
 * extends the {@link SafePWMMotor} base.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PWMSpeedController extends SafePWMMotor implements SpeedController {

	private boolean inverted = false;

	/**
	 * Creates a new PWM speed controller device for a given PWM port.
	 * 
	 * @param port port
	 * @param bounds pwm bounds
	 */
	public PWMSpeedController(PWM port, PWMBounds bounds) {
		super(port, bounds);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Speed is set by calling {@link #setSpeed(double)} from super. If the set to inverted, 
	 * the speed value used is inverted to the given value: 
	 * <p>
	 * {@code speed = -speed}
	 */
	@Override
	public void set(double speed) {
		setSpeed(inverted? -speed : speed);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the speed set by calling {@link #getSpeed()} from super.
	 */
	@Override
	public double get() {
		return getSpeed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInverted() {
		return inverted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
}
