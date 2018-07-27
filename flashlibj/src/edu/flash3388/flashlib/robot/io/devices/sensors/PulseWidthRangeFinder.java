package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.robot.io.IOFactory;
import edu.flash3388.flashlib.robot.io.devices.PulseCounter;

/**
 * Control class for a pulse width range finder sensor. Range finders are sensors used to measure distances between
 * them and an object in front of them. There are several ways range finders measure distances, for example: sound waves,
 * infrared, etc.
 * <p>
 * Pulse width range finders output the range value using a digital pulse. The length of the digital pulse corresponds to
 * the distance measured by the sensor. The sensitivity of the sensor is the conversion factor from pulse length
 * in microseconds to distance in centimeter. The value indicates the amount of microseconds per one centimeter of distance.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PulseWidthRangeFinder implements RangeFinder {

	private static final double DEFAULT_SENSITIVITY = 147.0 * 2.54;
	
	private PulseCounter counter;
	private double sensitivity;
	
	/**
	 * Creates a new pulse width range finder sensor.
	 * <p>
	 * A {@link IOFactory#createPulseCounter(int)} is created for the given port to measure pulse lengths from the
	 * sensor to convert into distance.
	 * <p>
	 * The sensitivity of the sensor is used to convert from pulse lengths in microseconds into distance. The sensitivity
	 * used is {@value #DEFAULT_SENSITIVITY}.
	 * 
	 * 
	 * @param port digital input port
	 */
	public PulseWidthRangeFinder(int port) {
		this(port, DEFAULT_SENSITIVITY);
	}
	/**
	 * Creates a new pulse width range finder sensor.
	 * <p>
	 * A {@link IOFactory#createPulseCounter(int)} is created for the given port to measure pulse lengths from the 
	 * sensor to convert into distance.
	 * <p>
	 * The sensitivity of the sensor is used to convert from pulse lengths in microseconds into distance.
	 * 
	 * 
	 * @param port digital input port
	 * @param sensitivity sensitivity in microseconds/centimeter
	 */
	public PulseWidthRangeFinder(int port, double sensitivity) {
		this.counter = IOFactory.createPulseCounter(port);
		this.sensitivity = sensitivity;
	}
	/**
	 * Creates a new pulse width range finder sensor.
	 * <p>
	 * The given pulse counter is used to measure pulse lengths from the sensor to convert into distance.
	 * <p>
	 * The sensitivity of the sensor is used to convert from pulse lengths in microseconds into distance. The sensitivity
	 * used is {@value #DEFAULT_SENSITIVITY}.
	 * 
	 * @param counter pulse counter for the input port
	 */
	public PulseWidthRangeFinder(PulseCounter counter) {
		this(counter, DEFAULT_SENSITIVITY);
	}
	/**
	 * Creates a new pulse width range finder sensor.
	 * <p>
	 * The given pulse counter is used to measure pulse lengths from the sensor to convert into distance.
	 * <p>
	 * The sensitivity of the sensor is used to convert from pulse lengths in microseconds into distance.
	 * 
	 * @param counter pulse counter for the input port
	 * @param sensitivity sensitivity in microseconds/centimeter
	 */
	public PulseWidthRangeFinder(PulseCounter counter, double sensitivity) {
		this.counter = counter;
		this.sensitivity = sensitivity;
	}
	
	/**
	 * Gets the sensor sensitivity value.
	 * <p>
	 * Sensitivity is used to convert the pulse length from the input port to range in centimeters. The value
	 * indicates the amount of microseconds per centimeter.
	 * 
	 * @return sensitivity in microseconds per centimeter
	 */
	public double getSensitivity(){
		return sensitivity;
	}
	/**
	 * Sets the sensor sensitivity value.
	 * <p>
	 * Sensitivity is used to convert the pulse length from the input port to range in centimeters. The value
	 * indicates the amount of microseconds per centimeter.
	 * 
	 * @param sensitivity sensitivity in microseconds per centimeter
	 */
	public void setSensitivity(double sensitivity){
		this.sensitivity = sensitivity;
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
	 * The length of the last pulse measured is read from the pulse counter, converted into microseconds and
	 * divided by the sensitivity value to get the distance.
	 */
	@Override
	public double getRangeCM() {
		return counter.getPulseLength() * 1e6 / sensitivity;
	}
}
