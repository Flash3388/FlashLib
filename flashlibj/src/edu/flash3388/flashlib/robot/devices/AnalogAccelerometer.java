package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.robot.control.PIDSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Control class for an analog accelerometer sensor. Accelerometer is a linear acceleration sensor,
 * used to measure the linear acceleration of an object it is placed on in one or more axes. 
 * <p>
 * Analog accelerometers are capable of measuring acceleration for one axis only. Acceleration from them is read 
 * using an analog input port. The voltage in the port corresponds to and acceleration and is converted to acceleration
 * measured in G by dividing the it by a scale factor of voltage per G. The sensor has a zero G voltage which is
 * the voltage when acceleration is 0.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class AnalogAccelerometer implements DoubleSource, PIDSource, IOPort{

	private static final double DEFAULT_SENSITIVITY = 1.0;
	private static final double DEFAULT_ZERO_VOLTAGE = 2.5;
	
	private AnalogInput input;
	private double zeroGvoltage;
	private double voltsPerG;
	
	/**
	 * Creates a new analog accelerometer sensor for a given analog input port. 
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into acceleration. The defined voltage output
	 * where acceleration is 0 is {@value #DEFAULT_ZERO_VOLTAGE} and the conversion factor from volts to acceleration in G 
	 * is {@value #DEFAULT_SENSITIVITY}.
	 * <p>
	 * An {@link AnalogInput} object is created for the given port using {@link IOFactory} by calling
	 * {@link IOFactory#createAnalogInputPort(int)} and passing it the given port number.
	 * 
	 * @param port analog input port to which the sensor is connected
	 */
	public AnalogAccelerometer(int port) {
		this(port, DEFAULT_ZERO_VOLTAGE, DEFAULT_SENSITIVITY);
	}
	/**
	 * Creates a new analog accelerometer sensor for a given analog input port. 
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into acceleration. The other 2 parameters
	 * are configuration parameters of the sensor. The first is the voltage output by the sensor when the
	 * acceleration is 0, the second is the conversion value from volts to acceleration is G.
	 * <p>
	 * An {@link AnalogInput} object is created for the given port using {@link IOFactory} by calling
	 * {@link IOFactory#createAnalogInputPort(int)} and passing it the given port number.
	 * 
	 * @param port analog input port to which the sensor is connected
	 * @param zeroGVoltage voltage when acceleration is 0
	 * @param voltsPerG conversion factor from volts to G acceleration.
	 */
	public AnalogAccelerometer(int port, double zeroGVoltage, double voltsPerG) {
		this.input = IOFactory.createAnalogInputPort(port);
		this.zeroGvoltage = zeroGVoltage;
		this.voltsPerG = voltsPerG;
	}
	/**
	 * Creates a new analog accelerometer sensor for a given analog input port. 
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into acceleration. The defined voltage output
	 * where acceleration is 0 is {@value #DEFAULT_ZERO_VOLTAGE} and the conversion factor from volts to acceleration in G 
	 * is {@value #DEFAULT_SENSITIVITY}.
	 * 
	 * @param input analog input port to which the sensor is connected
	 */
	public AnalogAccelerometer(AnalogInput input) {
		this(input, DEFAULT_ZERO_VOLTAGE, DEFAULT_SENSITIVITY);
	}
	/**
	 * Creates a new analog accelerometer sensor for a given analog input port. 
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into acceleration. The other 2 parameters
	 * are configuration parameters of the sensor. The first is the voltage output by the sensor when the
	 * acceleration is 0, the second is the conversion value from volts to acceleration is G.
	 * 
	 * @param input analog input port to which the sensor is connected
	 * @param zeroGVoltage voltage when acceleration is 0
	 * @param voltsPerG conversion factor from volts to G acceleration.
	 */
	public AnalogAccelerometer(AnalogInput input, double zeroGVoltage, double voltsPerG) {
		this.input = input;
		this.zeroGvoltage = zeroGVoltage;
		this.voltsPerG = voltsPerG;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the analog input port.
	 */
	@Override
	public void free() {
		if(input != null)
			input.free();
		input = null;
	}
	
	/**
	 * Sets the sensor sensitivity.
	 * <p>
	 * The sensitivity value indicates the conversion factor from voltage to acceleration.
	 * 
	 * @param voltsPerG sensor volts per G scale factor.
	 */
	public void setSensitivity(double voltsPerG){
		this.voltsPerG = voltsPerG;
	}
	/**
	 * Gets the sensor sensitivity.
	 * <p>
	 * The sensitivity value indicates the conversion factor from voltage to acceleration.
	 * 
	 * @return sensor volts per G scale factor.
	 */
	public double getSensitivity(){
		return voltsPerG;
	}
	
	/**
	 * Sets the sensor zero acceleration voltage.
	 * <p>
	 * This value indicates at which voltage the acceleration measured is 0.
	 * 
	 * @param volts voltage where acceleration is 0.
	 */
	public void setZeroVoltage(double volts){
		this.zeroGvoltage = volts;
	}
	/**
	 * Gets the sensor zero acceleration voltage.
	 * <p>
	 * This value indicates at which voltage the acceleration measured is 0.
	 * 
	 * @return voltage where acceleration is 0.
	 */
	public double getZeroVoltage(){
		return zeroGvoltage;
	}
	
	/**
	 * Gets the acceleration value measured by the sensor in G (9.8 meters per second squared).
	 * <p>
	 * The acceleration is calculated by reading the current voltage on the port, getting its offset from 
	 * the defined zero G voltage and dividing the offset by the scale factor.
	 * 
	 * @return acceleration in G.
	 */
	public double getAcceleration(){
		return (input.getVoltage() - zeroGvoltage) / voltsPerG;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the value of {@link #getAcceleration()}.
	 */
	@Override
	public double pidGet() {
		return getAcceleration();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the value of {@link #getAcceleration()}.
	 */
	@Override
	public double get() {
		return getAcceleration();
	}
}
