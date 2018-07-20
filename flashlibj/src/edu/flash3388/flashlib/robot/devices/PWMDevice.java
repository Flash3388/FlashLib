package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.math.Mathf;

/**
 * Control class for PWM-controlled electronic devices. This class should be extended by electronic devices who use
 * PWM and not instantiated.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PWMDevice implements IOPort{

	private PWM port;
	private double center, min, max, deadbandMin, deadbandMax;
	private boolean eliminateDeadband = false;
	
	/**
	 * Creates a new PWM device control for a given PWM port number. The port
	 * object is created by calling {@link IOFactory#createPWMPort(int)}.
	 * 
	 * @param port PWM port number
	 */
	public PWMDevice(int port) {
		this.port = IOFactory.createPWMPort(port);
	}
	/**
	 * Creates a new PWM device control for a given PWM port.
	 * 
	 * @param port the PWM port
	 */
	public PWMDevice(PWM port) {
		if(port == null)
			throw new NullPointerException("PWM port is null");
		this.port = port;
	}
	
	private double getCenter(){
		return center;
	}
	private double getPositiveScaleFactor(){
		return getMaxPositive() - getMinPositive();
	}
	private double getMaxPositive(){
		return max;
	}
	private double getMinPositive(){
		return eliminateDeadband? deadbandMax : center + 0.01;
	}
	private double getNegativeScaleFactor(){
		return getMaxNegative() - getMinNegative();
	}
	private double getMaxNegative(){
		return eliminateDeadband? deadbandMin : center - 0.01;
	}
	private double getMinNegative(){
		return min;
	}
	private double getFullScaleFactor(){
		return getMaxPositive() - getMinNegative();
	}
	
	private void setDutyCycle(double duty){
		port.setDuty(duty);
	}
	private double getDutyCycle(){
		return port.getDuty();
	}
	
	/**
	 * Sets the data bounds for the PWM device, configuring it for use. Using those values, 
	 * speed and position data are converted to duty cycle which is then written to the port.
	 * <p>
	 * The maximum and minimum bounds indicate the smallest and largest time cycles which can be detected by the device.
	 * Those bounds (in milliseconds) are used to determine the duty cycle which will indicate the minimal possible
	 * and maximal possible values. The center value indicates the duty cycle which corresponds to 0.0.
	 * <p>
	 * If wanted it is possible to eliminate out a deadband zone of values around the center. If eliminated, those
	 * values are not used in consideration when calculating the duty cycle to be used.
	 * 
	 * @param max the maximum bound of the PWM value in milliseconds
	 * @param deadbandMax the maximum bound  for the deadband
	 * @param center the center of the PWM value in milliseconds
	 * @param deadbandMin the minimum bound for the deadband
	 * @param min the minimum bound of the PWM value in milliseconds
	 */
	protected void setBounds(double max, double deadbandMax, double center, double deadbandMin, double min){
		double looptime = 1000.0 / port.getFrequency();
		this.max = max / looptime;
		this.min = min / looptime;
		this.center = center / looptime;
		this.deadbandMin = deadbandMin / looptime;
		this.deadbandMax = deadbandMax / looptime;
	}
	/**
	 * Sets the frequency of the PWM port used by the device.
	 * 
	 * @param frequency PWM frequency in HZ.
	 * @see PWM#setFrequency(double)
	 */
	protected void setFrequency(double frequency){
		port.setFrequency(frequency);
	}
	/**
	 * Gets the frequency of the PWM port used by the device.
	 * 
	 * @return PWM frequency in HZ.
	 * @see PWM#getFrequency()
	 */
	protected double getFrequency(){
		return port.getFrequency();
	}
	
	/**
	 * Sets whether or not to eliminate the value deadband. If eliminated, 
	 * a set of values around center defined by {@link #setBounds(double, double, double, double, double)} is
	 * used as bounds for converting speed values to PWM duty cycles.
	 * 
	 * @param eliminate true to enable, false to disable
	 */
	protected void setEliminateDeadband(boolean eliminate) {
		eliminateDeadband = eliminate;
	}
	/**
	 * Gets whether or not to eliminate the value deadband. If eliminated, 
	 * a set of values around center defined by {@link #setBounds(double, double, double, double, double)} is
	 * used as bounds for converting speed values to PWM duty cycles.
	 * 
	 * @return true if enabled, false if disabled
	 */
	protected boolean isDeadbandEliminated(){
		return eliminateDeadband;
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
	 * <p>
	 * {@code
	 * 		minPositive + speed * positiveScaleFactor
	 * }
	 * <p>
	 * If the value is negative, the output duty cycle
	 * is the maximum negative value plus the speed value times the negative scale factor:
	 * <p>
     * {@code
	 * 		maxNegative + speed * negativeScaleFactor
	 * }
	 * <p>
	 * The speed value is 0.0, the output duty cycle is the center value.
	 * 
	 * @param speed output speed value between -1.0 and 1.0.
	 */
	public void setSpeed(double speed) {
		speed = Mathf.constrain(speed, -1.0, 1.0);
		if(speed == 0.0)
			setDutyCycle(getCenter());
		else if(speed > 0.0)
			setDutyCycle(getMinPositive() + speed * getPositiveScaleFactor());
		else
			setDutyCycle(getMaxNegative() + speed * getNegativeScaleFactor());
	}
	/**
	 * Sets the device position between 0.0 and 1.0.
	 * <p>
	 * The position value is converted into duty cycle using the defined PWM bounds for
	 * the device. The output duty cycle is equal to the minimum negative value, adding to it 
	 * the position multiplied by the full bound scale factor:
	 * <p>
	 * {@code
	 * 		minNegative + position * fullScaleFactor
	 * }
	 * 
	 * @param pos device position between 0.0 and 1.0.
	 */
	public void setPosition(double pos){
		if(pos < 0.0)
			pos = Math.abs(pos);
		pos = Mathf.constrain(pos, 0.0, 1.0);
		
		setDutyCycle(getMinNegative() + pos * getFullScaleFactor());
	}
	
	/**
	 * Gets the speed value set as output to the device controlled by this PWM.
	 * <p>
	 * The output speed is converted from the output duty cycle using the bounds configured
	 * to this device. 
	 * <p>
	 * If the value is in the positive output range, the output speed is calculated using the positive scale
	 * of the PWM output range:
	 * <p>
	 * {@code
	 * 		(duty - minPositive) / positiveScaleFactor
	 * }
	 * <p>
	 * If the value is in the negative output range, the output speed is calculated using the negative scale
	 * of the PWM output range:
	 * {@code
	 * 		(duty - maxNegative) / negativeScaleFactor
	 * }
	 * <p>
	 * If the value is at the center or 0.0, the returned value is 0.0.
	 * 
	 * @return the current speed output between -1.0 and 1.0
	 */
	public double getSpeed() {
		double duty = getDutyCycle();
		
		if(duty == 0.0 || duty == getCenter())
			return 0.0;
		if(duty > getMaxPositive())
			return 1.0;
		if(duty < getMinNegative())
			return -1.0;
		if(duty > getMinPositive())
			return (duty - getMinPositive()) / getPositiveScaleFactor();
		if(duty < getMaxNegative())
			return (duty - getMaxNegative()) / getNegativeScaleFactor();
		return 0.0;
	}
	/**
	 * Gets the current position output to the device controller by this class.
	 * <p>
	 * The output position value is calculated from the current duty cycle set to the PWM port using
	 * the PWM bounds configured.
	 * <p>
	 * The value is calculated using the full bound range:
	 * {@code
	 * 		(duty - minNegative) / fullRangeScale
	 * }
	 * 
	 * @return the output position value between 0.0 and 1.0
	 */
	public double getPosition(){
		double duty = getDutyCycle();
		
		if(duty > getMaxPositive())
			return 1.0;
		if(duty < getMinNegative())
			return -1.0;
		
		return (duty - getMinNegative()) / getFullScaleFactor();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the PWM port used.
	 */
	@Override
	public void free() {
		if(port != null)
			port.free();
		port = null;
	}
}
