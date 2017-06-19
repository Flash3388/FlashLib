package edu.flash3388.flashlib.vision;

import java.util.Map;

/**
 * Filers out contours by their color. Can work for HSV or RGB filtering.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#filterHsv(int, int, int, int, int, int)
 * @see VisionSource#filterRgb(int, int, int, int, int, int)
 */
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
	public void parseParameters(Map<String, FilterParam> parameters) {
		hsv = FilterParam.getBooleanValue(parameters.get("hsv"));
		min1 = (short) FilterParam.getIntValue(parameters.get("min1"));
		min2 = (short) FilterParam.getIntValue(parameters.get("min2"));
		min3 = (short) FilterParam.getIntValue(parameters.get("min3"));
		max1 = (short) FilterParam.getIntValue(parameters.get("max1"));
		max2 = (short) FilterParam.getIntValue(parameters.get("max2"));
		max3 = (short) FilterParam.getIntValue(parameters.get("max3"));
	}
	@Override
	public FilterParam[] getParameters() {
		return new FilterParam[]{
				new FilterParam.BooleanParam("hsv", hsv),
				new FilterParam.IntParam("min1", min1),
				new FilterParam.IntParam("max1", max1),
				new FilterParam.IntParam("min2", min2),
				new FilterParam.IntParam("max2", max2),
				new FilterParam.IntParam("min3", min3),
				new FilterParam.IntParam("max3", max3)
		};
	}
}
