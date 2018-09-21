package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.io.PWM;

/**
 * Control class for a PWM-controlled speed controller device. This class integrates 
 * {@link PWMController} and {@link SpeedController} into one.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PWMSpeedController extends PWMController implements SpeedController {

	private boolean mInverted;

	/**
	 * Creates a new PWM speed controller device for a given PWM port.
	 * 
	 * @param port port
	 * @param pulseBounds pwm bounds
     * @param pwmFrequency frequency of the PWM in Hz
	 */
	public PWMSpeedController(PWM port, PWMBounds pulseBounds, double pwmFrequency) {
		super(port, pulseBounds, pwmFrequency);

		mInverted = false;
	}

    /**
     * Sets the speed of the device between -1.0 and 1.0. Negative values indicate backward scheduling, positive
     * values indicate forward scheduling, 0.0 indicates stopping.
     * <p>
     * The speed value is converted to a PWM duty cycle by using the bounds configured to the device.
     * If the given value is positive, the output duty cycle is calculated by getting the minimum positive scale
     * and adding it the speed value times the positive scale factor:
     * <pre>
     *      minPositive + speed * positiveScaleFactor
     * </pre>
     * If the value is negative, the output duty cycle
     * is the maximum negative value plus the speed value times the negative scale factor:
     * <pre>
     *      maxNegative + speed * negativeScaleFactor
     * </pre>
     * The speed value is 0.0, the output duty cycle is the center value.
     *
     * @param speed output speed value between -1.0 and 1.0.
     */
	@Override
	public void set(double speed) {
	    if (speed < -1.0 || speed > 1.0) {
	        throw new IllegalArgumentException("Invalid speed: " + speed);
        }

	    speed = mInverted ? -speed : speed;

        PWMBounds bounds = getBounds();

        if(speed == 0.0) {
            setDutyCycle(bounds.getCenter());
        } else if(speed > 0.0) {
            setDutyCycle(bounds.getMinPositive() + speed * bounds.getPositiveScaleFactor());
        } else {
            setDutyCycle(bounds.getMaxNegative() + speed * bounds.getNegativeScaleFactor());
        }
	}

    /**
     * Gets the speed value set as output to the device controlled by this PWM.
     * <p>
     * The output speed is converted from the output duty cycle using the bounds configured
     * to this device.
     * <p>
     * If the value is in the positive output range, the output speed is calculated using the positive scale
     * of the PWM output range:
     * <pre>
     * (duty - minPositive) / positiveScaleFactor
     * </pre>
     * If the value is in the negative output range, the output speed is calculated using the negative scale
     * of the PWM output range:
     * <pre>
     * (duty - maxNegative) / negativeScaleFactor
     * </pre>
     * If the value is at the center or 0.0, the returned value is 0.0.
     *
     * @return the current speed output between -1.0 and 1.0
     */
	@Override
	public double get() {
        double duty = getDutyCycle();

        PWMBounds bounds = getBounds();

        if(duty == 0.0 || duty == bounds.getCenter()) {
            return 0.0;
        }
        if(duty > bounds.getMaxPositive()) {
            return 1.0;
        }
        if(duty < bounds.getMinNegative()) {
            return -1.0;
        }
        if(duty > bounds.getMinPositive()) {
            return (duty - bounds.getMinPositive()) / bounds.getPositiveScaleFactor();
        }
        if(duty < bounds.getMaxNegative()) {
            return (duty - bounds.getMaxNegative()) / bounds.getNegativeScaleFactor();
        }

        return 0.0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInverted() {
		return mInverted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInverted(boolean inverted) {
		mInverted = inverted;
	}
}
