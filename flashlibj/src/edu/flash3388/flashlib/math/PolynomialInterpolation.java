package edu.flash3388.flashlib.math;

/**
 * Represents a base for polynomial interpolation. 
 * <p>
 * In numerical analysis, polynomial interpolation is the interpolation of a given data set by a 
 * polynomial: given some points, find a polynomial which goes exactly through these points.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * @see <a href="https://en.wikipedia.org/wiki/Polynomial_interpolation">https://en.wikipedia.org/wiki/Polynomial_interpolation</a>
 */
public abstract class PolynomialInterpolation extends Interpolation {
	
	private Double[] values, keys;
	private boolean valueUpdated = false;
	
	
	public PolynomialInterpolation(){
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
		if(updateRequired()){
			if(values.length != getMap().size()){
				values = new Double[getMap().size()];
				keys = new Double[getMap().size()];
			}
			getMap().values().toArray(values);
			getMap().keySet().toArray(keys);
			
			sort(keys, values);
			valueUpdated = true;
		}
	}
	private boolean updateRequired(){
		return values == null || !valueUpdated;
	}
	
	/**
	 * Gets the function value stored at a given index. This represents the y variable and is
	 * ordered in respect to the x variable.
	 * 
	 * @param idx index of the value
	 * @return the mapped value
	 */
	protected double getValue(int idx){
		return values[idx];
	}
	
	/**
	 * Gets the function value stored at a given index. This represents the x variable and is
	 * ordered from smallest to largest.
	 * 
	 * @param idx index of the value
	 * @return the mapped value
	 */
	protected double getKey(int idx){
		return keys[idx];
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void sort(Object[]...as){
		Object[] main = as[0];
		for (int i = 0; i < main.length; i++) {
			for (int j = 0; j < main.length-1; j++) {
				if(((Comparable)main[j]).compareTo(main[j+1]) > 0){
					for (int j2 = 0; j2 < as.length; j2++) {
						Object t = as[j2][j];
						as[j2][j] = as[j2][j+1];
						as[j2][j+1] = t;
					}
				}
			}
		}
	}
}
