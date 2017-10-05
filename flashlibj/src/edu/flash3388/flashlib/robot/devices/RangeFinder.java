package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Interface for range finder sensors. Range finders are used for measuring distance.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface RangeFinder extends DoubleSource, PIDSource, IOPort{
	/**
	 * Gets the distance measured by the sensor in centimeters. 
	 * @return distance by centimeters
	 */
	double getRangeCM();
	
	@Override
	default double pidGet() {
		return getRangeCM();
	}
	@Override
	default double get() {
		return getRangeCM();
	}
}
