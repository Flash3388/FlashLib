package com.flash3388.flashlib.io.devices.sensors;

import com.flash3388.flashlib.io.AnalogAccumulator;
import com.flash3388.flashlib.io.AnalogInput;
import com.flash3388.flashlib.io.devices.Gyro;

import java.io.IOException;
import java.util.Objects;

/**
 * Control class for an analog gyroscope sensor. Gyroscope sensors measure angular rotation and are used to measure
 * the angular position in one or more axes of an object they are placed on.
 * <p>
 * Analog gyroscopes are capable of measuring angular rotation in a single axis. Data can be read from them through an analog
 * input channel. The voltage output of the sensor indicates angular speed in degrees per second and is updated at a constant
 * rate. This value can then be accumulated into an angular position. The voltage output has a zero speed value which
 * indicates when the speed is zero.
 * <p>
 * In order to keep the sensor value updated as necessary, this classes uses an {@link AnalogAccumulator} object
 * which accumulates the speed values read from the port.
 *
 * @since FlashLib 1.2.0
 */
public class AnalogGyro implements Gyro {

    public static final double DEFAULT_SENSITIVITY = 0.007;
    public static final long CALIBRATION_TIME_MS = 500;
	
	private AnalogInput mInputPort;
	private AnalogAccumulator mAccumulator;
	
	private double mOffset;
	private double mSensitivity;
	private int mCenter;

    /**
     * Creates a new analog gyroscope sensor for a given analog input port.
     * <p>
     * The given port is used to read voltage from the sensor to convert into angular rotation. A valid port must have
     * an {@link AnalogAccumulator} object returned when calling {@link AnalogInput#getAccumulator()}. If this call returns
     * null, the port is not valid to be used for this class and an {@link IllegalArgumentException} is thrown.
     * <p>
     * For gyroscope sensitivity, gyro center voltage and gyro offset given values are used.
     *
     * @param port an analog input port
     * @param sensitivity sensor sensitivity in volts per degree per second
     * @param center uncalibrated sensor center voltage.
     * @param offset uncalibrated sensor offset voltage.
     */
    public AnalogGyro(AnalogInput port, double sensitivity, double center, double offset) {
        mInputPort = port;
        mSensitivity = sensitivity;
        mCenter = port.voltsToValue(center);
        mOffset = port.voltsToValue(offset);

        mAccumulator = port.getAccumulator();
        Objects.requireNonNull(mAccumulator, "Failed to retrieve accumulator for port, cannot use analog gyro");

        mAccumulator.enable();
    }

    /**
     * Creates a new analog gyroscope sensor for a given analog input port.
     * <p>
     * The given port is used to read voltage from the sensor to convert into angular rotation. A valid port must have
     * an {@link AnalogAccumulator} object returned when calling {@link AnalogInput#getAccumulator()}. If this call returns
     * null, the port is not valid to be used for this class and an {@link IllegalArgumentException} is thrown.
     * <p>
     * For gyro center voltage and gyro offset given values are used. Sensor sensitivity used is {@value #DEFAULT_SENSITIVITY}.
     *
     * @param port an analog input port
     * @param center uncalibrated sensor center voltage.
     * @param offset uncalibrated sensor offset voltage.
     */
    public AnalogGyro(AnalogInput port, double center, double offset){
        this(port, DEFAULT_SENSITIVITY, center, offset);
    }

	/**
	 * Creates a new analog gyroscope sensor for a given analog input port.
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into angular rotation. A valid port must have
	 * an {@link AnalogAccumulator} object returned when calling {@link AnalogInput#getAccumulator()}. If this call returns
	 * null, the port is not valid to be used for this class and an {@link IllegalArgumentException} is thrown.
	 * <p>
	 * Sensor center and offset should be calculated by calling {@link #calibrate()}. Sensitivity used is {@value #DEFAULT_SENSITIVITY}.
	 * 
	 * @param port an analog input port.
	 */
	public AnalogGyro(AnalogInput port) {
		mInputPort = port;
		mSensitivity = DEFAULT_SENSITIVITY;
		
		mAccumulator = port.getAccumulator();
		Objects.requireNonNull(mAccumulator, "Failed to retrieve accumulator for port, cannot use analog gyro");

		mAccumulator.enable();
	}
	
	/**
	 * Calibrates the sensor values, determining the gyro center and offset value. In this phase the gyro should remain
	 * stable and not move. Samples from the sensor are measured, the average value at this phase indicates the gyro center.
	 * This process takes approximately {@value #CALIBRATION_TIME_MS} seconds.
     *
     * @throws InterruptedException if the current thread was interrupted while waiting for calibration
	 */
	public void calibrate() throws InterruptedException {
		mAccumulator.reset();
		
		Thread.sleep(CALIBRATION_TIME_MS);
		
		long value = mAccumulator.getValue();
		int count = mAccumulator.getCount();

		mCenter = (int)((double)value / (double)count);
		mOffset = ((double)value / (double)count) - mCenter;

		mAccumulator.setCenter(mCenter);
		mAccumulator.reset();
	}
	
	/**
	 * Sets the sensor sensitivity. 
	 * <p>
	 * The sensitivity value is the conversion factor from voltage to angular speed.
	 * 
	 * @param sensitivity conversion factor from voltage to angular speed
	 */
	public void setSensitivity(double sensitivity){
		mSensitivity = sensitivity;
	}

	/**
	 * Gets the sensor sensitivity. 
	 * <p>
	 * The sensitivity value is the conversion factor from voltage to angular speed.
	 * 
	 * @return conversion factor from voltage to angular speed
	 */
	public double getSensitivity(){
		return mSensitivity;
	}
	
	/**
	 * Gets the gyro offset voltage set during calibration to use as a future preset.
	 *
	 * @return the current offset voltage
	 */
	public double getOffset(){
		return mInputPort.valueToVolts((int)mOffset);
	}

	/**
	 * Gets the gyro center voltage set during calibration to use as a future preset.
	 *
	 * @return the current center voltage
	 */
	public double getCenterVoltage(){
		return mInputPort.valueToVolts(mCenter);
	}

	/**
	 * Gets the gyro center value set during calibration to use as a future preset.
	 *
	 * @return the current center value
	 */
	public int getCenter(){
		return mCenter;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Resets the analog accumulator by calling {@link AnalogAccumulator#reset()}.
	 */
	@Override
	public void reset() {
		mAccumulator.reset();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Reads the current value and count from the analog accumulator and converts those values
	 * into an angle.
	 */
	@Override
	public double getAngle() {
		long value = mAccumulator.getValue() - (long)(mAccumulator.getCount() * mOffset);

        return ((value * mInputPort.getMaxVoltage()) / mInputPort.getMaxValue()) /
                (mInputPort.getSampleRate() * mSensitivity);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Reads the current voltage from the port and converts it into a rotation rate measured 
	 * by the sensor in degrees per second.
	 */
	@Override
	public double getRate() {
		double value = ((mInputPort.getValue() - (mCenter + mOffset)) * mInputPort.getMaxVoltage()) /
				mInputPort.getMaxVoltage();
		return (value / mSensitivity);
	}

    /**
     * {@inheritDoc}
     * <p>
     * Releases the analog input port.
     */
    @Override
    public void close() throws IOException {
        if (mInputPort != null) {
            mInputPort.close();
            mInputPort = null;
            mAccumulator = null;
        }
    }
}
