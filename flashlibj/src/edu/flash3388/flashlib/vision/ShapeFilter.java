package edu.flash3388.flashlib.vision;

public class ShapeFilter extends ProcessingFilter{
	private byte amount, vertecies;
	private double accuracy;

	public ShapeFilter(){}
	public ShapeFilter(int amount, int vertecies, double accuracy){
		this.amount = (byte) amount;
		this.vertecies = (byte) vertecies;
		this.accuracy = accuracy;
	}
	
	public int getAmount(){
		return amount;
	}
	public void setAmount(int amount){
		this.amount = (byte) amount;
	}
	public int getVertecies(){
		return vertecies;
	}
	public void setVertecies(int vertecies){
		this.vertecies = (byte) vertecies;
	}
	public double getAccuracy(){
		return accuracy;
	}
	public void setAccuracy(double accuracy){
		this.accuracy = accuracy;
	}
	
	@Override
	public void process(VisionSource source) {
		if(amount <= 0)
			source.detectShapes(vertecies, accuracy);
		else
			source.detectShapes(amount, vertecies, accuracy);
	}
	@Override
	public void parseParameters(double[] parameters) {
		if(parameters.length != 3)
			throw new IllegalArgumentException("Arguments invalid! Require 3");
		
		amount = (byte) parameters[0];
		vertecies = (byte) parameters[1];
		accuracy = parameters[2];
	}
	@Override
	public double[] getParameters() {
		return new double[] {amount, vertecies, accuracy};
	}
}
