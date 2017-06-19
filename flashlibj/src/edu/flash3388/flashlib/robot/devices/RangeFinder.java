package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for range finder sensors. Range finders are used for measuring distance.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface RangeFinder {
	/**
	 * Gets the distance measured by the sensor in centimeters. 
	 * @return distance by centimeters
	 */
	double getRangeCM();
}
