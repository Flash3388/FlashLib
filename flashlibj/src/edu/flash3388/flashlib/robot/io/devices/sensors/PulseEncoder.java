package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.robot.io.Counter;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.Time;

/**
 * Pulse based encoder sensor.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PulseEncoder implements Encoder {

	private static final int DEFAULT_REST_TIMEOUT = 200;
	
	private EncoderDataType mDataType;
	
	private Counter mCounter;
	private final Clock mClock;
	
	private double mDistancePerPulse;
	private int mPulsesPerRevolution;
	
	private int mLastCount;
	private long mLastCheckTime;
	private long mRestTimeout;
	private boolean mShouldCheckRest;

	public PulseEncoder(Counter counter, Clock clock, int pulsesPerRevolution, double distancePerPulse) {
		mCounter = counter;
		mClock = clock;
		mDistancePerPulse = distancePerPulse;
		mPulsesPerRevolution = pulsesPerRevolution;

		mShouldCheckRest = false;
		mRestTimeout = DEFAULT_REST_TIMEOUT;
        mDataType = EncoderDataType.Rate;
        mLastCheckTime = Time.INVALID_TIME;

		reset();
	}
	
	private void checkRest(){
		if(mShouldCheckRest){
			long time = mClock.currentTimeMillis();
			int count = getRaw();
			
			if(mLastCheckTime == Time.INVALID_TIME) {
				mLastCheckTime = time;
			}

			if(mLastCount == count && time - mLastCheckTime >= mRestTimeout) {
				reset();
			}
			
			mLastCheckTime = time;
			mLastCount = count;
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
		this.mDistancePerPulse = distancePerPulse;
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
		return mDistancePerPulse;
	}
	
	/**
	 * Sets the amount of pulses that the encoder outputs per one revolution of the rotating object measured. This
	 * value can be found in the encoder sensor datasheet.
	 * 
	 * @param pulsesPerRevolution amount of pulses per rotation revolution.
	 */
	public void setPulsesPerRevolution(int pulsesPerRevolution){
		this.mPulsesPerRevolution = pulsesPerRevolution;
	}

	/**
	 * Gets the amount of pulses that the encoder outputs per one revolution of the rotating object measured. This
	 * value can be found in the encoder sensor datasheet.
	 * 
	 * @return amount of pulses per rotation revolution.
	 */
	public int getPulsesPerRevolution(){
		return mPulsesPerRevolution;
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
		mShouldCheckRest = restCheck;

		if(restCheck){
			mLastCheckTime = 0;
			mLastCount = 0;
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
		return mShouldCheckRest;
	}
	
	/**
	 * Sets the timeout for rest check. This value indicates the time after which if not new pulses
	 * have been measured by the pulse counter a reset should be performed.
	 * 
	 * @param timeout timeout in milliseconds.
	 */
	public void setRestTimeout(int timeout){
		mRestTimeout = timeout;
	}

	/**
	 * Gets the timeout for rest check. This value indicates the time after which if not new pulses
	 * have been measured by the pulse counter a reset should be performed.
	 * 
	 * @return timeout in milliseconds.
	 */
	public long getRestTimeout(){
		return mRestTimeout;
	}
			
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the pulse counter object used.
	 */
	@Override
	public void free() {
		if(mCounter != null) {
			mCounter.free();
			mCounter = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Resets the pulse counter by calling {@link Counter#reset()}.
	 */
	@Override
	public void reset() {
		mLastCheckTime = 0;
		mLastCount = 0;
		mCounter.reset();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The linear velocity is calculated by dividing the distance passed per pulse by the time
	 * between the last two pulses measured by the pulse counter.
	 */
	@Override
	public double getVelocity(){
		return mDistancePerPulse / mCounter.getPulsePeriod();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The rotation rate is calculated by dividing the angular distance passed between two pulses by the time
	 * measured between the last two pulses. This value is then converted to RPM. In this case, the angular distance
	 * is 360 degrees since there only one pulse per revolution.
	 */
	@Override
	public double getRate() {
		checkRest();
		
		if(getRaw() == 0) {
			return 0.0;
		}

		return (60.0 * mPulsesPerRevolution) / mCounter.getPulsePeriod();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The distance passed is calculated by multiplying the amount of pulses counted by the distance passed
	 * by the object per one pulse.
	 */
	@Override
	public double getDistance() {
		return mCounter.get() * mDistancePerPulse;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The pulse count is returned by getting it from the pulse counter object used, calling {@link Counter#get()}.
	 */
	@Override
	public int getRaw() {
		return mCounter.get();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The direction is received by calling {@link Counter#getDirection()}.
	 */
	@Override
	public boolean getDirection(){
		return mCounter.getDirection();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EncoderDataType getDataType() {
		return mDataType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDataType(EncoderDataType type) {
		mDataType = type;
	}
}
