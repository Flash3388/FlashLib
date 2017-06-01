package edu.flash3388.flashlib.vision;

public class ColorFilter extends ProcessingFilter{

	private short min1, max1, min2, max2, min3, max3;
	private boolean hsv;

	public ColorFilter(){}
	public ColorFilter(boolean hsv, int min1, int max1, int min2, int max2, int min3, int max3){
		this.hsv = hsv;
		this.min1 = (short) min1;
		this.max1 = (short) max1;
		this.max2 = (short) max2;
		this.min2 = (short) min2;
		this.max3 = (short) max3;
		this.min3 = (short) min3;
	}
	
	public void set(int min1, int max1, int min2, int max2, int min3, int max3){
		this.min1 = (short) min1;
		this.max1 = (short) max1;
		this.max2 = (short) max2;
		this.min2 = (short) min2;
		this.max3 = (short) max3;
		this.min3 = (short) min3;
	}
	
	public boolean isHsv(){
		return hsv;
	}
	public void setHsv(boolean hsv){
		this.hsv = hsv;
	}
	
	public int getMin1(){
		return min1;
	}
	public void setMin1(int min1){
		this.min1 = (short) min1;
	}
	public int getMax1(){
		return max1;
	}
	public void setMax1(int max1){
		this.max1 = (short) max1;
	}
	
	public int getMin2(){
		return min2;
	}
	public void setMin2(int min2){
		this.min2 = (short) min2;
	}
	public int getMax2(){
		return max2;
	}
	public void setMax2(int max2){
		this.max2 = (short) max2;
	}
	
	public int getMin3(){
		return min3;
	}
	public void setMin3(int min3){
		this.min3 = (short) min3;
	}
	public int getMax3(){
		return max3;
	}
	public void setMax3(int max3){
		this.max3 = (short) max3;
	}
	
	@Override
	public void process(VisionSource source) {
		if(hsv)
			source.filterHsv(min1, min2, min3, max1, max2, max3);
		else
			source.filterRgb(min1, min2, min3, max1, max2, max3);
	}
	@Override
	public void parseParameters(double[] parameters) {
		if(parameters.length != 7)
			throw new IllegalArgumentException("Arguments invalid! Require 7");
		
		hsv = parameters[0] == 1;
		min1 = (short) parameters[1];
		max1 = (short) parameters[2];
		min2 = (short) parameters[3];
		max2 = (short) parameters[4];
		min3 = (short) parameters[5];
		max3 = (short) parameters[6];
	}
	@Override
	public double[] getParameters() {
		return new double[] {hsv? 1:0, min1, max1, min2, max2, min3, max3};
	}
}
