package edu.flash3388.flashlib.vision;

public class ClosestToCenterFilter extends ProcessingFilter{
	private byte amount = 0;
	
	public ClosestToCenterFilter(){}
	public ClosestToCenterFilter(int amount){
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
		source.closestToCenterFrame(amount);
	}
	@Override
	public void parseParameters(double[] parameters) {
		if(parameters.length != 1)
			throw new IllegalArgumentException("Requires 1 argument");
		amount = (byte) parameters[0];
	}
	@Override
	public double[] getParameters() {
		return new double[]{amount};
	}
}
