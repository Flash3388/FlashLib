package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for encoder sensors. Encoders are used for measuring angular rotation of axes or wheels.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Encoder {
	/**
	 * Gets the rate of rotation measured by the encoder.
	 * @return rate of rotation
	 */
	double getRate();
	/**
	 * Gets the distance passed by the wheel the sensor measures. 
	 * <p>
	 * Distance is measured by the amount of completed revolutions measured by the sensor. This value 
	 * is than multiplied by a constant which describes the distance passed per one revolution. For wheels,
	 * that value is equal to the circumference of the wheel.
	 * </p>
	 * @return distance passed
	 */
	double getDistance();
}
