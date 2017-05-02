package edu.flash3388.flashlib.math;

public class LinearInterpolation extends Interpolation{
	
	public LinearInterpolation(double keyMargin){
		super(keyMargin);
	}
	
	@Override
	public double interpolate(double x2){
		if(x2 % getKeyMargin() == 0)
			return getValue(x2);
		
		double x1 = Mathd.roundToMultiplier(x2, getKeyMargin(), false);
		double x3 = Mathd.roundToMultiplier(x2, getKeyMargin(), true);
		double y1 = getValue(x1);
		double y3 = getValue(x3);
		return (((x2 - x1)*(y3-y1))/(x3-x1))+y1;
	}
}
