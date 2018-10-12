package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.util.Resource;

import java.util.function.DoubleSupplier;

/**
 * Interface for range finder sensors. Range finders are sensors used to measure distances between
 * them and an object in front of them. There are several ways range finders measure distances, for example: sound waves,
 * infrared, etc.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface RangeFinder extends Resource, DoubleSupplier {

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
    default double getAsDouble() {
        return getRangeCM();
    }
}
