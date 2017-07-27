package edu.flash3388.flashlib.vision;

import java.util.Map;

/**
 * Filers out contours by their color. Works with grayscale.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * 
 * @see VisionSource#filterColorRange(int, int)
 */
public class GrayFilter extends ProcessingFilter{

	private int min, max;

	public GrayFilter(){}
	public GrayFilter(int min, int max){
		set(min, max);
	}
	
	public void set(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	public int getMin(){
		return min;
	}
	public void setMin(int min){
		this.min = min;
	}
	public int getMax(){
		return max;
	}
	public void setMax(int max){
		this.max = max;
	}
	
	@Override
	public void process(VisionSource source) {
		source.convertGrayscale();
		
		source.filterColorRange(min, max);
	}
	@Override
	public void parseParameters(Map<String, VisionParam> parameters) {
		min = VisionParam.getIntValue(parameters.get("min"));
		max = VisionParam.getIntValue(parameters.get("max"));
	}
	@Override
	public VisionParam[] getParameters() {
		return new VisionParam[]{
				new VisionParam.IntParam("min", min),
				new VisionParam.IntParam("max", max)
		};
	}
}
