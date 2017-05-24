package edu.flash3388.flashlib.vision;

public class LowestFilter extends ProcessingFilter{
	private byte amount;

	public LowestFilter(){}
	public LowestFilter(int amount){
		this.amount = (byte) amount;
	}
	
	public int getAmount(){
		return amount;
	}
	public void setAmount(int amount){
		this.amount = (byte) amount;
	}
	
	@Override
	public void process(VisionSource source) {
		if(amount <= 0)
			amount = 1;
		source.lowestContours(amount);
	}
	@Override
	public void parseParameters(double[] parameters) {
		if(parameters.length != 1)
			throw new IllegalArgumentException("Arguments invalid! Require 1");
		
		amount = (byte) parameters[0];
	}
	@Override
	public double[] getParameters() {
		return new double[] {amount};
	}
}
