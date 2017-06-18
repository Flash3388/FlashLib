package edu.flash3388.flashlib.vision;

import java.util.Map;

/**
 * Filers out contours by their proximity to a coordinate in the frame.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#closestToCoordinate(double, double, int)
 */
public class CoordinateFilter extends ProcessingFilter{
	private double x, y;
	private byte amount = 0;

	public CoordinateFilter(){}
	public CoordinateFilter(double x, double y, int amount){
		this.x = x;
		this.y = y;
		this.amount = (byte) amount;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	public int getAmount(){
		return amount;
	}
	public void setAmount(int amount){
		this.amount = (byte) amount;
	}
	
	@Override
	public void process(VisionSource source) {
		source.closestToCoordinate(x, y, amount);
	}
	@Override
	public void parseParameters(Map<String, FilterParam> parameters) {
		amount = (byte) FilterParam.getIntValue(parameters.get("amount"));
		x = FilterParam.getDoubleValue(parameters.get("x"));
		y = FilterParam.getDoubleValue(parameters.get("y"));
	}
	@Override
	public FilterParam[] getParameters() {
		return new FilterParam[]{
				new FilterParam.IntParam("amount", amount),
				new FilterParam.DoubleParam("x", x),
				new FilterParam.DoubleParam("y", y)
		};
	}
}
