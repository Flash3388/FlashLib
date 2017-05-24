package edu.flash3388.flashlib.vision;

public class RgbFilter extends ProcessingFilter{

	private short minR, maxR, minG, maxG, minB, maxB;

	public RgbFilter(){}
	public RgbFilter(int minR, int maxR, int minG, int maxG, int minB, int maxB){
		this.minR = (short) minR;
		this.maxR = (short) maxR;
		this.maxG = (short) maxG;
		this.minG = (short) minG;
		this.maxB = (short) maxB;
		this.minB = (short) minB;
	}
	
	public void set(int minR, int maxR, int minG, int maxG, int minB, int maxB){
		this.minR = (short) minR;
		this.maxR = (short) maxR;
		this.maxG = (short) maxG;
		this.minG = (short) minG;
		this.maxB = (short) maxB;
		this.minB = (short) minB;
	}
	
	public int getMinR(){
		return minR;
	}
	public void setMinR(int minR){
		this.minR = (short) minR;
	}
	public int getMaxR(){
		return maxR;
	}
	public void setMaxR(int maxR){
		this.maxR = (short) maxR;
	}
	
	public int getMinG(){
		return minG;
	}
	public void setMinG(int minG){
		this.minG = (short) minG;
	}
	public int getMaxG(){
		return maxG;
	}
	public void setMaxG(int maxG){
		this.maxG = (short) maxG;
	}
	
	public int getMinB(){
		return minB;
	}
	public void setMinB(int minB){
		this.minB = (short) minB;
	}
	public int getMaxB(){
		return maxB;
	}
	public void setMaxB(int maxB){
		this.maxB = (short) maxB;
	}
	
	@Override
	public void process(VisionSource source) {
		source.filterRgb(minR, minG, minB, maxR, maxG, maxB);
	}
	@Override
	public void parseParameters(double[] parameters) {
		if(parameters.length != 6)
			throw new IllegalArgumentException("Arguments invalid! Require 6");
		
		minR = (short) parameters[0];
		maxR = (short) parameters[1];
		minG = (short) parameters[2];
		maxG = (short) parameters[3];
		minB = (short) parameters[4];
		maxB = (short) parameters[5];
	}
	@Override
	public double[] getParameters() {
		return new double[] {minR, maxR, minG, maxG, minB, maxB};
	}
}
