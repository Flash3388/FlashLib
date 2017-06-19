package edu.flash3388.flashlib.math;

import edu.flash3388.flashlib.util.FlashUtil;

public class PolynomialInterpolation extends Interpolation{
	
	private Double[] values, keys;
	private boolean valueUpdated = false;
	
	/**
	 * Creates an interpolation object for polynomial functions. Sets the key margin to a given value.
	 * Uses Newton's formula for Polynomial interpolation.
	 * <p>
	 * In numerical analysis, polynomial interpolation is the interpolation of a given data set by a 
	 * polynomial: given some points, find a polynomial which goes exactly through these points.
	 * </p>
	 * <p>
	 * n the mathematical field of numerical analysis, a Newton polynomial, named after its inventor 
	 * Isaac Newton, is the interpolation polynomial for a given set of data points in the Newton form. 
	 * The Newton polynomial is sometimes called Newton's divided differences interpolation polynomial 
	 * because the coefficients of the polynomial are calculated using divided differences.
	 * </p>
	 * @param keyMargin the key margin
	 * @see <a href="https://en.wikipedia.org/wiki/Newton_polynomial">https://en.wikipedia.org/wiki/Newton_polynomial</a>
	 * @see <a href="https://en.wikipedia.org/wiki/Polynomial_interpolation">https://en.wikipedia.org/wiki/Polynomial_interpolation</a>
	 */
	public PolynomialInterpolation(double keyMargin){
		super(keyMargin);
		values = new Double[0];
		keys = new Double[0];
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(double key, double value){
		super.put(key, value);
		valueUpdated = false;
	}
	
	/**
	 * Updates values from the map and prepare them for use in the calculation. Called automatically
	 * by {@link #interpolate(double)}
	 */
	public void updateValues(){
		if(valuesUpdated()){
			if(values.length != getMap().size()){
				values = new Double[getMap().size()];
				keys = new Double[getMap().size()];
			}
			getMap().values().toArray(values);
			getMap().keySet().toArray(keys);
			
			FlashUtil.sort(keys, values);
			valueUpdated = true;
		}
	}
	private boolean valuesUpdated(){
		return values == null || !valueUpdated;
	}
	private double firstOrderDifference(int k){
		return values[k+1] - values[k];
	}
	private double orderDifference(int k, int order){
		if(order == 0) return firstOrderDifference(k);
		return orderDifference(k+1, order-1) - orderDifference(k, order-1);
	}
	
	@Override
	public double interpolate(double x){
		if(x % getKeyMargin() == 0)
			return getValue(x);
		updateValues();
		
		double factorial = 1, numerator = 1, denumerator = 1, result = values[0];
		for (int i = 0; i < values.length-2; i++) {
			factorial *= (i+1);
			denumerator *= getKeyMargin();
			numerator *= (x - keys[i]);
			result += (orderDifference(0, i) / factorial) * (numerator / denumerator);
		}
		return result;
	}
}
