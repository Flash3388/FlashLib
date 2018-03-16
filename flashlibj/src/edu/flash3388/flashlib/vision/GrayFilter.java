package edu.flash3388.flashlib.vision;

/**
 * Filers out contours by their color. Works with grayscale.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * 
 * @see VisionSource#filterColorRange(int, int)
 */
public class GrayFilter implements VisionFilter{

	/**
	 * Indicates the minimum value of the color filter.
	 */
	private int min;
	/**
	 * Indicates the maximum value of the color filter.
	 */
	private int max;

	public GrayFilter(){}
	public GrayFilter(int min, int max){
		set(min, max);
	}
	
	public void set(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	@Override
	public void process(VisionSource source) {
		source.convertGrayscale();
		
		source.filterColorRange(min, max);
	}
}
