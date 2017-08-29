package edu.flash3388.flashlib.math;

/**
 * Interface for interpolations with equally spaced x values.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface MarginInterpolation {
	
	/**
	 * Sets the consistent value margin between 2 x coordinates. This value is used 
	 * in some interpolations.
	 * @param keyMargin the margin
	 */
	void setKeyMargin(double keyMargin);
	
	/**
	 * Gets the consistent value margin between 2 x coordinates. This value is used 
	 * in some interpolations.
	 * @return the margin
	 */
	double getKeyMargin();
}
