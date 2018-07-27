package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.robot.io.AnalogInput;
import edu.flash3388.flashlib.robot.io.IOFactory;

/**
 * Control class for an analog range finder sensor. Range finders are sensors used to measure distances between
 * them and an object in front of them. There are several ways range finders measure distances, for example: sound waves,
 * infrared, etc.
 * <p>
 * Analog range finders output the range value using an analog signal. The voltage on the port indicates the range
 * and can be converted to centimeters by dividing it by a sensitivity value. The sensitivity value is measured by 
 * volts/centimeter.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class AnalogRangeFinder implements RangeFinder {
	
	private AnalogInput input;
	private double sensitivity;
	
	/**
	 * Creates a new analog range finder sensor.
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into distance.
	 * <p>
	 * The sensitivity of the sensor is used to convert from analog voltage into distance.
	 * <p>
	 * An {@link AnalogInput} object is created for the given port using {@link IOFactory} by calling
	 * {@link IOFactory#createAnalogInputPort(int)} and passing it the given port number.
	 * 
	 * @param port analog input port
	 * @param sensitivity sensitivity in volts/centimeter
	 */
	public AnalogRangeFinder(int port, double sensitivity) {
		this.input = IOFactory.createAnalogInputPort(port);
		this.sensitivity = sensitivity;
	}
	/**
	 * Creates a new analog range finder sensor.
	 * <p>
	 * The given port is used to read voltage from the sensor to convert into distance.
	 * <p>
	 * The sensitivity of the sensor is used to convert from analog voltage into distance.
	 * 
	 * @param input analog input port
	 * @param sensitivity sensitivity in volts/centimeter
	 */
	public AnalogRangeFinder(AnalogInput input, double sensitivity) {
		this.input = input;
		this.sensitivity = sensitivity;
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
	 * Sets the sensor sensitivity value.
	 * <p>
	 * Sensitivity is used to convert the voltage from the input port to range in centimeters. The value
	 * indicates the amount of volts per centimeter.
	 * 
	 * @param sensitivity sensitivity in volts per centimeter
	 */
	public void setSensitivity(double sensitivity){
		this.sensitivity = sensitivity;
	}
	/**
	 * Gets the sensor sensitivity value.
	 * <p>
	 * Sensitivity is used to convert the voltage from the input port to range in centimeters. The value
	 * indicates the amount of volts per centimeter.
	 * 
	 * @return sensitivity in volts per centimeter
	 */
	public double getSensitivity(){
		return sensitivity;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The voltage on the input port is read and then divided by the sensitivity value
	 * to convert it from volts to centimeters.
	 */
	@Override
	public double getRangeCM() {
		return input.getVoltage() / sensitivity;
	}
}
