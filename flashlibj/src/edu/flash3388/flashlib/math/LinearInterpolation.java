package edu.flash3388.flashlib.math;

/**
 * Represents interpolation for linear functions. Extends {@link Interpolation}
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class LinearInterpolation extends Interpolation{
	
	/**
	 * Creates an interpolation object for linear functions. Sets the key margin to a given value.
	 * <p>
	 * In mathematics, linear interpolation is a method of curve fitting using linear polynomials 
	 * to construct new data points within the range of a discrete set of known data points.
	 * </p>
	 * @param keyMargin the key margin
	 * @see <a href="https://en.wikipedia.org/wiki/Linear_interpolation">https://en.wikipedia.org/wiki/Linear_interpolation</a>
	 */
	public LinearInterpolation(double keyMargin){
		super(keyMargin);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double interpolate(double x2){
		if(x2 % getKeyMargin() == 0)
			return getValue(x2);
		
		double x1 = Mathf.roundToMultiplier(x2, getKeyMargin(), false);
		double x3 = Mathf.roundToMultiplier(x2, getKeyMargin(), true);
		double y1 = getValue(x1);
		double y3 = getValue(x3);
		return (((x2 - x1)*(y3-y1))/(x3-x1))+y1;
	}
}
