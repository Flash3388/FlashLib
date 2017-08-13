package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;

/**
 * Filers out contours by their color. Works with grayscale.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * 
 * @see VisionSource#filterColorRange(int, int)
 */
public class GrayFilter extends VisionFilter{

	private IntegerProperty min = new SimpleIntegerProperty(), 
			max = new SimpleIntegerProperty();

	public GrayFilter(){}
	public GrayFilter(int min, int max){
		set(min, max);
	}
	
	public void set(int min, int max){
		this.min.set(min);
		this.max.set(max);
	}
	
	/**
	 * An {@link IntegerProperty}.
	 * Indicates the value of the minimum value of the color filter.
	 * @return the property
	 */
	public IntegerProperty minProperty(){
		return min;
	}
	/**
	 * An {@link IntegerProperty}.
	 * Indicates the value of the maximum value of the color filter.
	 * @return the property
	 */
	public IntegerProperty maxProperty(){
		return max;
	}
	
	@Override
	public void process(VisionSource source) {
		source.convertGrayscale();
		
		source.filterColorRange(min.get(), max.get());
	}
}
