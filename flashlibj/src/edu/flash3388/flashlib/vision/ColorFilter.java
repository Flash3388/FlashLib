package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleBooleanProperty;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;

/**
 * Filers out contours by their color. Can work for HSV or RGB filtering.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * 
 * @see VisionSource#filterColorRange(int, int, int, int, int, int)
 */
public class ColorFilter extends VisionFilter{

	private IntegerProperty min1 = new SimpleIntegerProperty(), 
			max1 = new SimpleIntegerProperty(), 
			min2 = new SimpleIntegerProperty(), 
			max2 = new SimpleIntegerProperty(), 
			min3 = new SimpleIntegerProperty(), 
			max3 = new SimpleIntegerProperty();
	private BooleanProperty hsv = new SimpleBooleanProperty();

	public ColorFilter(){}
	public ColorFilter(boolean hsv, int min1, int max1, int min2, int max2, int min3, int max3){
		set(min1, max1, min2, max2, min3, max3);
		this.hsv.set(hsv);
	}
	
	public void set(int min1, int max1, int min2, int max2, int min3, int max3){
		this.min1.set(min1);
		this.min2.set(min2);
		this.min3.set(min3);
		this.min1.set(max1);
		this.max2.set(max2);
		this.max3.set(max3);
	}
	
	public BooleanProperty hsvProperty(){
		return hsv;
	}
	
	public IntegerProperty min1Property(){
		return min1;
	}
	public IntegerProperty min2Property(){
		return min2;
	}
	public IntegerProperty min3Property(){
		return min3;
	}
	public IntegerProperty max1Property(){
		return max1;
	}
	public IntegerProperty max2Property(){
		return max2;
	}
	public IntegerProperty max3Property(){
		return max3;
	}
	
	@Override
	public void process(VisionSource source) {
		if(hsv.get())
			source.convertHsv();
		else source.convertRgb();
		
		source.filterColorRange(min1.get(), min2.get(), min3.get(), max1.get(), max2.get(), max3.get());
	}
}
