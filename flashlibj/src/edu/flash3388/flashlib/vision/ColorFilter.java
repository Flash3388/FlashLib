package edu.flash3388.flashlib.vision;

/**
 * Filers out contours by their color. Can work for HSV or RGB filtering.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * 
 * @see VisionSource#filterColorRange(int, int, int, int, int, int)
 */
public class ColorFilter extends VisionFilter{

	/**
	 * Indicates the minimum values of the color filter. 
	 * min[0] = Minimum Hue in HSV or minimum Red in RGB.
	 * min[1] = Minimum Saturation in HSV or minimum Green in RGB.
	 * min[2] = Minimum Value in HSV or minimum Blue in RGB.
	 */
	private int[] min = new int[3];
	/**
	 * Indicates the maximum values of the color filter. 
	 * max[0] = Maximum Hue in HSV or Maximum Red in RGB.
	 * max[1] = Maximum Saturation in HSV or Maximum Green in RGB.
	 * max[2] = Maximum Value in HSV or Maximum Blue in RGB.
	 */
	private int[] max = new int[3];
	/**
	 * Indicates whether to filter the image for HSV values or RGB values.
	 */
	private boolean hsv;

	public ColorFilter(){}
	public ColorFilter(boolean hsv, int[] min, int[] max){
		set(min, max);
		this.hsv = hsv;
	}
	public ColorFilter(boolean hsv, int min1, int max1, int min2, int max2, int min3, int max3){
		set(min1, max1, min2, max2, min3, max3);
		this.hsv = hsv;
	}
	
	public void set(int[] min, int[] max){
		if (min.length != this.min.length)
			throw new IllegalArgumentException("Min values array must have a length of " + this.min.length);
		if (max.length != this.max.length)
			throw new IllegalArgumentException("Max values array must have a length of " + this.max.length);
		
		System.arraycopy(min, 0, this.min, 0, this.min.length);
		System.arraycopy(max, 0, this.max, 0, this.max.length);
	}
	public void set(int min1, int max1, int min2, int max2, int min3, int max3){
		set(new int[] {min1, min2, min3}, new int[] {max1, max2, max3});
	}
	
	@Override
	public void process(VisionSource source) {
		if(hsv)
			source.convertHsv();
		else source.convertRgb();
		
		source.filterColorRange(min[0], min[1], min[2], max[0], max[1], max[2]);
	}
}
