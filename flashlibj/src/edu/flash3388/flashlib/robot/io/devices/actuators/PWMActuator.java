package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.io.PWM;
import edu.flash3388.flashlib.util.Resource;

/**
 * Control class for PWM-controlled electronic devices. This class should be extended by electronic devices who use
 * PWM and not instantiated.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PWMActuator implements Resource {

	private PWM mPort;
	private PWMBounds mBounds;

	/**
	 * Creates a new PWM device control for a given PWM port.
	 * 
	 * @param port the PWM port
	 * @param bounds the PWM port bounds
	 */
	public PWMActuator(PWM port, PWMBounds bounds) {
		mPort = port;
		mBounds = bounds;

		setBoundsForPort();
	}
	
	/**
	 * Disables the PWM device by setting the output duty cycle to 0.0.
	 */
	public void disable(){
		setDutyCycle(0.0);
	}
	
	/**
	 * Sets the speed of the device between -1.0 and 1.0. Negative values indicate backward scheduling, positive
	 * values indicate forward scheduling, 0.0 indicates stopping.
	 * <p>
	 * The speed value is converted to a PWM duty cycle by using the bounds configured to the device.
	 * If the given value is positive, the output duty cycle is calculated by getting the minimum positive scale
	 * and adding it the speed value times the positive scale factor:
	 * <pre>
	 * minPositive + speed * positiveScaleFactor
	 * </pre>
	 * If the value is negative, the output duty cycle
	 * is the maximum negative value plus the speed value times the negative scale factor:
	 * <pre>
	 * maxNegative + speed * negativeScaleFactor
	 * </pre>
	 * The speed value is 0.0, the output duty cycle is the center value.
	 * 
	 * @param speed output speed value between -1.0 and 1.0.
	 */
	public void setSpeed(double speed) {
		speed = Mathf.constrain(speed, -1.0, 1.0);

		if(speed == 0.0) {
			setDutyCycle(mBounds.getCenter());
		} else if(speed > 0.0) {
			setDutyCycle(mBounds.getMinPositive() + speed * mBounds.getPositiveScaleFactor());
		} else {
			setDutyCycle(mBounds.getMaxNegative() + speed * mBounds.getNegativeScaleFactor());
		}
	}

	/**
	 * Sets the device position between 0.0 and 1.0.
	 * <p>
	 * The position value is converted into duty cycle using the defined PWM bounds for
	 * the device. The output duty cycle is equal to the minimum negative value, adding to it 
	 * the position multiplied by the full bound scale factor:
	 * <pre>
	 * minNegative + position * fullScaleFactor
	 * </pre>
	 * 
	 * @param pos device position between 0.0 and 1.0.
	 */
	public void setPosition(double pos){
		if(pos < 0.0) {
			pos = Math.abs(pos);
		}

		pos = Mathf.constrain(pos, 0.0, 1.0);
		
		setDutyCycle(mBounds.getMinNegative() + pos * mBounds.getFullScaleFactor());
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
	public double getSpeed() {
		double duty = getDutyCycle();
		
		if(duty == 0.0 || duty == mBounds.getCenter()) {
			return 0.0;
		}
		if(duty > mBounds.getMaxPositive()) {
			return 1.0;
		}
		if(duty < mBounds.getMinNegative()) {
			return -1.0;
		}
		if(duty > mBounds.getMinPositive()) {
			return (duty - mBounds.getMinPositive()) / mBounds.getPositiveScaleFactor();
		}
		if(duty < mBounds.getMaxNegative()) {
			return (duty - mBounds.getMaxNegative()) / mBounds.getNegativeScaleFactor();
		}

		return 0.0;
	}

	/**
	 * Gets the current position output to the device controller by this class.
	 * <p>
	 * The output position value is calculated from the current duty cycle set to the PWM port using
	 * the PWM bounds configured.
	 * <p>
	 * The value is calculated using the full bound range:
	 * <pre>
	 * (duty - minNegative) / fullRangeScale
	 * </pre>
	 * 
	 * @return the output position value between 0.0 and 1.0
	 */
	public double getPosition(){
		double duty = getDutyCycle();
		
		if(duty > mBounds.getMaxPositive()) {
			return 1.0;
		}
		if(duty < mBounds.getMinNegative()) {
			return -1.0;
		}
		
		return (duty - mBounds.getMinNegative()) / mBounds.getFullScaleFactor();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the PWM port used.
	 */
	@Override
	public void free() {
		if (mPort != null) {
			mPort.free();
			mPort = null;
		}
	}

	/**
	 * Gets the frequency of the PWM port used by the device.
	 *
	 * @return PWM frequency in HZ.
	 * @see PWM#getFrequency()
	 */
	public double getFrequency(){
		return mPort.getFrequency();
	}

	/**
	 * Sets the frequency of the PWM port used by the device.
	 *
	 * @param frequency PWM frequency in HZ.
	 * @see PWM#setFrequency(double)
	 */
	protected void setFrequency(double frequency){
		mPort.setFrequency(frequency);
	}

	private void setBoundsForPort() {
		double looptime = 1000.0 / getFrequency();

		double max = mBounds.getMaxPositive() / looptime;
		double deadbandMin = mBounds.getDeadbandMax() / looptime;
		double center = mBounds.getCenter() / looptime;
		double deadbandMax = mBounds.getDeadbandMin() / looptime;
		double min = mBounds.getMinNegative() / looptime;

		mBounds.set(max, deadbandMax, center, deadbandMin, min);
	}

	private void setDutyCycle(double duty) {
		mPort.setDuty(duty);
	}

	private double getDutyCycle() {
		return mPort.getDuty();
	}
}
