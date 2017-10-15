package edu.flash3388.flashlib.math;

import java.util.Map;

/**
 * <p>
 * In numerical analysis, Lagrange polynomials are used for polynomial interpolation. For a given set of distinct
 * points x1 and numbers y1, the Lagrange polynomial is the 
 * polynomial of lowest degree that assumes at each point x1 the corresponding value y1 
 * (i.e. the functions coincide at each point). 
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * @see <a href="https://en.wikipedia.org/wiki/Lagrange_polynomial">https://en.wikipedia.org/wiki/Lagrange_polynomial</a>
 */
public class LagrangePolynomial extends PolynomialInterpolation{

	public LagrangePolynomial(Map<Double, Double> valuesMap) {
		super(valuesMap);
	}
	public LagrangePolynomial(){
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double interpolate(double x) {
		updateValues();
		
		double numerator, denumerator, result = 0;
		int i, j;
		
		for (i = 0; i < getMappedValuesCount(); i++) {
			numerator = 1;
			denumerator = 1;
			
			for (j = 0; j < getMappedValuesCount(); j++) {
				if(i == j)
					continue;
				numerator *= (x - getKey(j));
				denumerator *= (getKey(i) - getKey(j));
			}
			result += ((numerator / denumerator) * getValue(i));
		}
		
		
		return result;
	}
}
