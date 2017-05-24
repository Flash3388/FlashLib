package edu.flash3388.flashlib.vision;

public class CoordinateFilter extends ProcessingFilter{
	private double x, y;

	public CoordinateFilter(){}
	public CoordinateFilter(double x, double y){
		this.x = x;
		this.y = y;
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
	
	@Override
	public void process(VisionSource source) {
		source.closestToCoordinate(x, y);
	}
	@Override
	public void parseParameters(double[] parameters) {
		if(parameters.length != 2)
			throw new IllegalArgumentException("Arguments invalid! Require 2");
		
		x = parameters[0];
		y = parameters[1];
	}
	@Override
	public double[] getParameters() {
		return new double[] {x, y};
	}
}
