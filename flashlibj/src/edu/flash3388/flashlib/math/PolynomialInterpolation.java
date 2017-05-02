package edu.flash3388.flashlib.math;

import edu.flash3388.flashlib.util.Algorithms;

public class PolynomialInterpolation extends Interpolation{
	
	private Double[] values, keys;
	private boolean valueUpdated = false;
	
	public PolynomialInterpolation(double keyMargin){
		super(keyMargin);
	}
	
	@Override
	public void put(double key, double value){
		super.put(key, value);
		valueUpdated = false;
	}
	
	public void updateValues(){
		if(valuesUpdated()){
			values = getMap().values().toArray(new Double[0]);
			keys = getMap().keySet().toArray(new Double[0]);
			Algorithms.sort(keys, values);
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
