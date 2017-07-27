package edu.flash3388.flashlib.vision;

import java.util.Map;

/**
 * Filers out contours by their color. Can work for HSV or RGB filtering.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * 
 * @see VisionSource#filterColorRange(int, int, int, int, int, int)
 */
public class ColorFilter extends ProcessingFilter{

	private int min1, max1, min2, max2, min3, max3;
	private boolean hsv;

	public ColorFilter(){}
	public ColorFilter(boolean hsv, int min1, int max1, int min2, int max2, int min3, int max3){
		this.hsv = hsv;
		this.min1 = min1;
		this.max1 = max1;
		this.max2 = max2;
		this.min2 = min2;
		this.max3 = max3;
		this.min3 = min3;
	}
	
	public void set(int min1, int max1, int min2, int max2, int min3, int max3){
		this.min1 = min1;
		this.max1 = max1;
		this.max2 = max2;
		this.min2 = min2;
		this.max3 = max3;
		this.min3 = min3;
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
		this.min1 = min1;
	}
	public int getMax1(){
		return max1;
	}
	public void setMax1(int max1){
		this.max1 = max1;
	}
	
	public int getMin2(){
		return min2;
	}
	public void setMin2(int min2){
		this.min2 = min2;
	}
	public int getMax2(){
		return max2;
	}
	public void setMax2(int max2){
		this.max2 = max2;
	}
	
	public int getMin3(){
		return min3;
	}
	public void setMin3(int min3){
		this.min3 = min3;
	}
	public int getMax3(){
		return max3;
	}
	public void setMax3(int max3){
		this.max3 = max3;
	}
	
	@Override
	public void process(VisionSource source) {
		if(hsv)
			source.convertHsv();
		else source.convertRgb();
		
		source.filterColorRange(min1, min2, min3, max1, max2, max3);
	}
	@Override
	public void parseParameters(Map<String, VisionParam> parameters) {
		hsv = VisionParam.getBooleanValue(parameters.get("hsv"));
		min1 = VisionParam.getIntValue(parameters.get("min1"));
		min2 = VisionParam.getIntValue(parameters.get("min2"));
		min3 = VisionParam.getIntValue(parameters.get("min3"));
		max1 = VisionParam.getIntValue(parameters.get("max1"));
		max2 = VisionParam.getIntValue(parameters.get("max2"));
		max3 = VisionParam.getIntValue(parameters.get("max3"));
	}
	@Override
	public VisionParam[] getParameters() {
		return new VisionParam[]{
				new VisionParam.BooleanParam("hsv", hsv),
				new VisionParam.IntParam("min1", min1),
				new VisionParam.IntParam("max1", max1),
				new VisionParam.IntParam("min2", min2),
				new VisionParam.IntParam("max2", max2),
				new VisionParam.IntParam("min3", min3),
				new VisionParam.IntParam("max3", max3)
		};
	}
}
