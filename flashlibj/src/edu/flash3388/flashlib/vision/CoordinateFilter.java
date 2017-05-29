package edu.flash3388.flashlib.vision;

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
	public void parseParameters(double[] parameters) {
		if(parameters.length != 3)
			throw new IllegalArgumentException("Arguments invalid! Require 3");
		
		x = parameters[0];
		y = parameters[1];
		amount = (byte) parameters[2];
	}
	@Override
	public double[] getParameters() {
		return new double[] {x, y, amount};
	}
}
