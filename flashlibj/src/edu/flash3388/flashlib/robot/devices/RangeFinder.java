package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.robot.control.PIDSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Interface for range finder sensors. Range finders are sensors used to measure distances between
 * them and an object in front of them. There are several ways range finders measure distances, for example: sound waves,
 * infrared, etc.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface RangeFinder extends DoubleSource, PIDSource, IOPort{
	/**
	 * Gets the distance measured by the sensor in centimeters. 
	 * 
	 * @return distance by centimeters
	 */
	double getRangeCM();
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the value of {@link #getRangeCM()}.
	 */
	@Override
	default double pidGet() {
		return getRangeCM();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the value of {@link #getRangeCM()}.
	 */
	@Override
	default double get() {
		return getRangeCM();
	}
}
