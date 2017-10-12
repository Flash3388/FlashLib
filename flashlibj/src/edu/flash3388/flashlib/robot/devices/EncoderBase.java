package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Base for relative encoder sensors.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class EncoderBase implements Encoder{

	private static final int DEFAULT_REST_TIMEOUT = 200;
	
	private EncoderDataType pidType = EncoderDataType.Rate;
	
	private PulseCounter counter;
	
	private double distancePerPulse;
	private int pulsesPerRevolution;
	
	private int lastCount;
	private int lastCheckTime;
	private int restTimeout = DEFAULT_REST_TIMEOUT;
	private boolean checkRest = false;
	
	protected EncoderBase(int port, int pulsesPerRevolution, double distancePerPulse) {
		this.counter = IOFactory.createPulseCounter(port);
		this.distancePerPulse = distancePerPulse;
		this.pulsesPerRevolution = pulsesPerRevolution;
		
		checkQuadrature(false);
		reset();
	}
	protected EncoderBase(int upPort, int downPort, int pulsesPerRevolution, double distancePerPulse) {
		this.counter = IOFactory.createPulseCounter(upPort, downPort);
		this.distancePerPulse = distancePerPulse;
		this.pulsesPerRevolution = pulsesPerRevolution;
		
		checkQuadrature(false);
		reset();
	}
	protected EncoderBase(PulseCounter counter, int pulsesPerRevolution, double distancePerPulse, boolean quadrature) {
		this.counter = counter;
		this.distancePerPulse = distancePerPulse;
		this.pulsesPerRevolution = pulsesPerRevolution;
		
		checkQuadrature(quadrature);
		reset();
	}
	
	private void checkQuadrature(boolean quadrature){
		if(counter.isQuadrature() != quadrature){
			throw new IllegalArgumentException(quadrature? 
					"Expected a quadrature counter, isQuadrature returned false" :
					"Expected a non-quadrature counter, isQuadrature returned true"
					);
		}
	}
	
	private void checkRest(){
		if(checkRest){
			int time = FlashUtil.millisInt();
			int count = getRaw();
			
			if(lastCheckTime == 0)
				lastCheckTime = time;
			if(lastCount == count && time - lastCheckTime >= restTimeout)
				reset();
			
			lastCheckTime = time;
			lastCount = count;
		}
	}
	
	/**
	 * Sets the linear distance passed by the measured object per one pulse of the encoder. This value 
	 * depends on the amount of pulses the encoder outputs per rotation revolution and what object is measured.
	 * If a wheel is measured, the distance per pulse would be its circumference divided by the amount of pulses
	 * per revolution.
	 * 
	 * @param distancePerPulse distance passed per pulse in meters.
	 */
	public void setDistancePerPulse(double distancePerPulse){
		this.distancePerPulse = distancePerPulse;
	}
	/**
	 * Gets the linear distance passed by the measured object per one pulse of the encoder. This value 
	 * depends on the amount of pulses the encoder outputs per rotation revolution and what object is measured.
	 * If a wheel is measured, the distance per pulse would be its circumference divided by the amount of pulses
	 * per revolution.
	 * 
	 * @return distance passed per pulse in meters.
	 */
	public double getDistancePerPulse(){
		return distancePerPulse;
	}
	
	/**
	 * Sets the amount of pulses that the encoder outputs per one revolution of the rotating object measured. This
	 * value can be found in the encoder sensor datasheet.
	 * 
	 * @param pulsesPerRevolution amount of pulses per rotation revolution.
	 */
	public void setPulsesPerRevolution(int pulsesPerRevolution){
		this.pulsesPerRevolution = pulsesPerRevolution;
	}
	/**
	 * Gets the amount of pulses that the encoder outputs per one revolution of the rotating object measured. This
	 * value can be found in the encoder sensor datasheet.
	 * 
	 * @return amount of pulses per rotation revolution.
	 */
	public int getPulsesPerRevolution(){
		return pulsesPerRevolution;
	}
	
	/**
	 * Sets whether or not this encoder should perform rest checking when the rotation rate is measured.
	 * <p>
	 * If enabled, each time {@link #getRate()} is called a check will be performed to see if the encoder has
	 * measured movement since the last {@link #getRate()} call. If not, the pulse counter object is reset.
	 * <p>
	 * This feature is recommended to insure that when the measured object is not rotating, this class will
	 * not return values indicating rotation. This might happen depending on the pulse counter implementation
	 * used.
	 * 
	 * @param restCheck true to enable rest check, false otherwise.
	 */
	public void enableRestCheck(boolean restCheck){
		checkRest = restCheck;
		if(restCheck){
			lastCheckTime = 0;
			lastCount = 0;
		}
	}
	/**
	 * Gets whether or not this encoder should perform rest checking when the rotation rate is measured.
	 * <p>
	 * If enabled, each time {@link #getRate()} is called a check will be performed to see if the encoder has
	 * measured movement since the last {@link #getRate()} call. If not, the pulse counter object is reset.
	 * <p>
	 * This feature is recommended to insure that when the measured object is not rotating, this class will
	 * not return values indicating rotation. This might happen depending on the pulse counter implementation
	 * used.
	 * 
	 * @return true to enable rest check, false otherwise.
	 */
	public boolean isRestCheckEnabled(){
		return checkRest;
	}
	
	/**
	 * Sets the timeout for rest check. This value indicates the time after which if not new pulses
	 * have been measured by the pulse counter a reset should be performed.
	 * 
	 * @param timeout timeout in milliseconds.
	 */
	public void setRestTimeout(int timeout){
		restTimeout = timeout;
	}
	/**
	 * Gets the timeout for rest check. This value indicates the time after which if not new pulses
	 * have been measured by the pulse counter a reset should be performed.
	 * 
	 * @return timeout in milliseconds.
	 */
	public int getRestTimeout(){
		return restTimeout;
	}
			
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the pulse counter object used.
	 */
	@Override
	public void free() {
		if(counter != null)
			counter.free();
		counter = null;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Resets the pulse counter by calling {@link PulseCounter#reset()}.
	 */
	@Override
	public void reset() {
		lastCheckTime = 0;
		lastCount = 0;
		counter.reset();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The linear velocity is calculated by dividing the distance passed per pulse by the time
	 * between the last two pulses measured by the pulse counter.
	 */
	@Override
	public double getVelocity(){
		return distancePerPulse / counter.getPulsePeriod();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * The rotation rate is calculated by dividing the angular distance passed between two pulses by the time
	 * measured between the last two pulses. This value is then conveRted to RPM. In this case, the angular distance
	 * is 360 degrees since there only one pulse per revolution.
	 */
	@Override
	public double getRate() {
		checkRest();
		
		if(counter.get() == 0)
			return 0.0;
		return ((360.0 / pulsesPerRevolution) * 60.0) / counter.getPulsePeriod();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * The distance passed is calculated by multiplying the amount of pulses counted by the distance passed
	 * by the object per one pulse.
	 */
	@Override
	public double getDistance() {
		return counter.get() * distancePerPulse;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * The pulse count is returned by getting it from the pulse counter object used, calling {@link PulseCounter#get()}.
	 */
	@Override
	public int getRaw() {
		return counter.get();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * The direction is received by calling {@link PulseCounter#getDirection()}.
	 */
	@Override
	public boolean getDirection(){
		return counter.getDirection();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EncoderDataType getDataType() {
		return pidType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDataType(EncoderDataType type) {
		pidType = type;
	}
}
