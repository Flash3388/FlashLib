package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.robot.io.AnalogInput;
import edu.flash3388.flashlib.robot.io.IOFactory;
import edu.flash3388.flashlib.util.FlashUtil;

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
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class AnalogGyro extends GyroBase{
	
	private static final double DEFAULT_SENSITIVITY = 0.007;
	private static final double CALIBRATION_TIME = 0.5;
	
	private AnalogInput inputPort;
	private AnalogAccumulator accumulator;
	
	private double offset;
	private double sensitivity;
	private int center;
	
	/**
	 * Creates a new analog gyroscope sensor for a given analog input port.
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into angular rotation. A valid port must have
	 * an {@link AnalogAccumulator} object returned when calling {@link AnalogInput#getAccumulator()}. If this call returns
	 * null, the port is not valid to be used for this class and an {@link IllegalArgumentException} is thrown.
	 * <p>
	 * An {@link AnalogInput} object is created for the given port using {@link IOFactory} by calling
	 * {@link IOFactory#createAnalogInputPort(int)} and passing it the given port number.
	 * <p>
	 * Sensor center and offset are calculated by calling {@link #calibrate()}. Sensitivity used is {@value #DEFAULT_SENSITIVITY}.
	 * 
	 * @param port an analog input port 
	 * 
	 * @throws IllegalArgumentException if {@link AnalogInput#getAccumulator()} returned null.
	 */
	public AnalogGyro(int port){
		this.inputPort = IOFactory.createAnalogInputPort(port);
		
		this.accumulator = inputPort.getAccumulator();
		if(accumulator == null)
			throw new IllegalArgumentException("Failed to retreive accumulator for port, cannot use analog gyro");
		this.accumulator.enable();
		
		calibrate();
	}
	/**
	 * Creates a new analog gyroscope sensor for a given analog input port.
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into angular rotation. A valid port must have
	 * an {@link AnalogAccumulator} object returned when calling {@link AnalogInput#getAccumulator()}. If this call returns
	 * null, the port is not valid to be used for this class and an {@link IllegalArgumentException} is thrown.
	 * <p>
	 * An {@link AnalogInput} object is created for the given port using {@link IOFactory} by calling
	 * {@link IOFactory#createAnalogInputPort(int)} and passing it the given port number.
	 * <p>
	 * For gyro center voltage and gyro offset given values are used. Sensitivity used is {@value #DEFAULT_SENSITIVITY}.
	 * 
	 * @param port an analog input port 
	 * @param center uncalibrated sensor center voltage.
	 * @param offset uncalibrated sensor offset voltage.
	 * 
	 * @throws IllegalArgumentException if {@link AnalogInput#getAccumulator()} returned null.
	 */
	public AnalogGyro(int port, double center, double offset){
		this(port, DEFAULT_SENSITIVITY, center, offset);
	}
	/**
	 * Creates a new analog gyroscope sensor for a given analog input port.
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into angular rotation. A valid port must have
	 * an {@link AnalogAccumulator} object returned when calling {@link AnalogInput#getAccumulator()}. If this call returns
	 * null, the port is not valid to be used for this class and an {@link IllegalArgumentException} is thrown.
	 * <p>
	 * An {@link AnalogInput} object is created for the given port using {@link IOFactory} by calling
	 * {@link IOFactory#createAnalogInputPort(int)} and passing it the given port number.
	 * <p>
	 * For gyroscope sensitivity, gyro center voltage and gyro offset given values are used.
	 * 
	 * @param port an analog input port 
	 * @param sensetivity sensor sensitivity in volts per degree per second
	 * @param center uncalibrated sensor center voltage.
	 * @param offset uncalibrated sensor offset voltage.
	 * 
	 * @throws IllegalArgumentException if {@link AnalogInput#getAccumulator()} returned null.
	 */
	public AnalogGyro(int port, double sensetivity, double center, double offset) {
		this.inputPort = IOFactory.createAnalogInputPort(port);
		this.sensitivity = sensetivity;
		this.center = inputPort.voltsToValue(center);
		this.offset = inputPort.voltsToValue(offset);
		
		this.accumulator = inputPort.getAccumulator();
		if(accumulator == null)
			throw new IllegalArgumentException("Failed to retreive accumulator for port, cannot use analog gyro");
		this.accumulator.enable();
	}
	
	/**
	 * Creates a new analog gyroscope sensor for a given analog input port.
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into angular rotation. A valid port must have
	 * an {@link AnalogAccumulator} object returned when calling {@link AnalogInput#getAccumulator()}. If this call returns
	 * null, the port is not valid to be used for this class and an {@link IllegalArgumentException} is thrown.
	 * <p>
	 * Sensor center and offset are calculated by calling {@link #calibrate()}. Sensitivity used is {@value #DEFAULT_SENSITIVITY}.
	 * 
	 * @param port an analog input port 
	 * 
	 * @throws IllegalArgumentException if {@link AnalogInput#getAccumulator()} returned null.
	 */
	public AnalogGyro(AnalogInput port){
		this.inputPort = port;
		
		this.accumulator = port.getAccumulator();
		if(accumulator == null)
			throw new IllegalArgumentException("Failed to retreive accumulator for port, cannot use analog gyro");
		this.accumulator.enable();
		
		calibrate();
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
	 * 
	 * @throws IllegalArgumentException if {@link AnalogInput#getAccumulator()} returned null.
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
	 * For gyroscope sensitivity, gyro center voltage and gyro offset given values are used.
	 * 
	 * @param port an analog input port 
	 * @param sensetivity sensor sensitivity in volts per degree per second
	 * @param center uncalibrated sensor center voltage.
	 * @param offset uncalibrated sensor offset voltage.
	 * 
	 * @throws IllegalArgumentException if {@link AnalogInput#getAccumulator()} returned null.
	 */
	public AnalogGyro(AnalogInput port, double sensetivity, double center, double offset) {
		this.inputPort = port;
		this.sensitivity = sensetivity;
		this.center = port.voltsToValue(center);
		this.offset = port.voltsToValue(offset);
		
		this.accumulator = port.getAccumulator();
		if(accumulator == null)
			throw new IllegalArgumentException("Failed to retreive accumulator for port, cannot use analog gyro");
		this.accumulator.enable();
	}
	
	/**
	 * Calibrates the sensor values, determining the gyro center and offset value. In this phase the gyro should remain
	 * stable and not move. Samples from the sensor are measured, the average value at this phase indicates the gyro center.
	 * This process takes approximately {@value #CALIBRATION_TIME} seconds.
	 */
	public void calibrate(){
		accumulator.reset();
		
		FlashUtil.delay(CALIBRATION_TIME);
		
		long value = accumulator.getValue();
		int count = accumulator.getCount();
		
		center = (int)((double)value / (double)count);
		offset = ((double)value / (double)count) - center;
		
		accumulator.setCenter(center);
		accumulator.reset();
	}
	
	/**
	 * Sets the sensor sensitivity. 
	 * <p>
	 * The sensitivity value is the conversion factor from voltage to angular speed.
	 * 
	 * @param sensitivity conversion factor from voltage to angular speed
	 */
	public void setSensitivity(double sensitivity){
		this.sensitivity = sensitivity;
	}
	/**
	 * Gets the sensor sensitivity. 
	 * <p>
	 * The sensitivity value is the conversion factor from voltage to angular speed.
	 * 
	 * @return conversion factor from voltage to angular speed
	 */
	public double getSensitivity(){
		return sensitivity;
	}
	
	/**
	 * Gets the gyro offset voltage set during calibration to use as a future preset.
	 *
	 * @return the current offset voltage
	 */
	public double getOffset(){
		return inputPort.valueToVolts((int)offset);
	}
	/**
	 * Gets the gyro center voltage set during calibration to use as a future preset.
	 *
	 * @return the current center voltage
	 */
	public double getCenterVoltage(){
		return inputPort.valueToVolts(center);
	}
	/**
	 * Gets the gyro center value set during calibration to use as a future preset.
	 *
	 * @return the current center value
	 */
	public int getCenter(){
		return center;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the analog input port.
	 */
	@Override
	public void free() {
		if(inputPort != null)
			inputPort.free();
		inputPort = null;
		accumulator = null;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Resets the analog accumulator by calling {@link AnalogAccumulator#reset()}.
	 */
	@Override
	public void reset() {
		accumulator.reset();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Reads the current value and count from the analog accumulator and converts those values
	 * into an angle.
	 */
	@Override
	public double getAngle() {
		long value = accumulator.getValue() - (long)(accumulator.getCount() * offset);
		
		double scaledValue = ((value * inputPort.getMaxVoltage()) / inputPort.getMaxValue()) / 
				(inputPort.getSampleRate() * sensitivity);
		return scaledValue;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Reads the current voltage from the port and converts it into a rotation rate measured 
	 * by the sensor in degrees per second.
	 */
	@Override
	public double getRate() {
		double value = ((inputPort.getValue() - (center + offset)) * inputPort.getMaxVoltage()) / 
				inputPort.getMaxVoltage();
		return (value / sensitivity);
	}
}
