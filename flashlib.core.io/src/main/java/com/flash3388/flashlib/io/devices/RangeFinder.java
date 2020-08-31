package com.flash3388.flashlib.io.devices;

import java.io.Closeable;
import java.util.function.DoubleSupplier;

/**
 * Interface for range finder sensors. Range finders are sensors used to measure distances between
 * them and an object in front of them. There are several ways range finders measure distances, for example: sound waves,
 * infrared, etc.
 *
 * @since FlashLib 1.0.0
 */
public interface RangeFinder extends Closeable, DoubleSupplier {

	/**
	 * Gets the distance measured by the sensor in centimeters. 
	 * 
	 * @return distance by centimeters
	 */
	double getRangeCm();

    /**
     * {@inheritDoc}
     * <p>
     * Returns the value of {@link #getRangeCm()}.
     */
    @Override
    default double getAsDouble() {
        return getRangeCm();
    }
}
