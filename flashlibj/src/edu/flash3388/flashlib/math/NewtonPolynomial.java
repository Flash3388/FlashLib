package edu.flash3388.flashlib.math;

/**
 * <p>
 * In the mathematical field of numerical analysis, a Newton polynomial, named after its inventor 
 * Isaac Newton, is the interpolation polynomial for a given set of data points in the Newton form. 
 * The Newton polynomial is sometimes called Newton's divided differences interpolation polynomial 
 * because the coefficients of the polynomial are calculated using divided differences.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * @see <a href="https://en.wikipedia.org/wiki/Newton_polynomial">https://en.wikipedia.org/wiki/Newton_polynomial</a>
 */
public class NewtonPolynomial extends PolynomialInterpolation implements MarginInterpolation{
	
	private double keyMargin;
	
	/**
	 * <p>
	 * Creates an interpolation object for polynomial functions. Sets the key margin to a given value.
	 * Uses Newton's formula for Polynomial interpolation.
	 * </p>
	 * @param keyMargin the key margin
	 */
	public NewtonPolynomial(double keyMargin){
		this.keyMargin = keyMargin;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setKeyMargin(double keyMargin){
		this.keyMargin = keyMargin;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double getKeyMargin(){
		return keyMargin;
	}
	
	
	private double firstOrderDifference(int k){
		return getValue(k+1) - getValue(k);
	}
	private double orderDifference(int k, int order){
		if(order == 0) return firstOrderDifference(k);
		return orderDifference(k+1, order-1) - orderDifference(k, order-1);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double interpolate(double x){
		if(x % getKeyMargin() == 0)
			return getValue(x);
		updateValues();
		
		double factorial = 1, numerator = 1, denumerator = 1, result = getValue(0);
		for (int i = 0; i < getMappedValuesCount()-2; i++) {
			factorial *= (i+1);
			denumerator *= getKeyMargin();
			numerator *= (x - getKey(i));
			result += (orderDifference(0, i) / factorial) * (numerator / denumerator);
		}
		return result;
	}
}
