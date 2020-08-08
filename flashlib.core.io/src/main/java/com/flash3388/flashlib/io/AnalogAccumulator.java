package com.flash3388.flashlib.io;

/**
 * An accumulator for an {@link AnalogInput} port. This is used to accumulate values from the port,
 * allowing view of all data throughout time, which is extremely useful when rapid sampling of the port
 * is required.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface AnalogAccumulator {

	/**
	 * Enables the accumulator for use. While enabled the accumulator
	 * accumulates data from the port when it is sampled by the implementation.
	 */
	void enable();

	/**
	 * Disables the accumulator, stopping data accumulation.
	 */
	void disable();
	
	/**
	 * Resets the accumulator, setting its value and count to zero.
	 */
	void reset();
	
	/**
	 * Sets the accumulator value center. This value indicates the expected center
	 * of accumulated data. While the accumulator is enabled, data is accumulated as an
	 * offset of the center.
	 * 
	 * @param value analog value representing the accumulator center
	 */
	void setCenter(int value);
	
	/**
	 * Gets the value accumulator from the analog input port so far as analog value and
	 * not voltage.
	 * 
	 * @return accumulated data
	 */
	long getValue();

	/**
	 * Gets the sample count of accumulated values.
	 * @return the sample count.
	 */
	int getCount();
}
