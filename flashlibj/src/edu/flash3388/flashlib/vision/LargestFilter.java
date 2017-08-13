package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;


/**
 * Filers out contours by their size.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#largestContours(int)
 */
public class LargestFilter extends VisionFilter{
	
	private SimpleIntegerProperty amount = new SimpleIntegerProperty();

	public LargestFilter(){}
	public LargestFilter(int amount){
		this.amount.set(amount);
	}
	
	/**
	 * An {@link IntegerProperty}.
	 * Indicates the maximum amount of contours to leave after the filter process.
	 * Must be non-negative
	 * @return the property
	 */
	public IntegerProperty amountProperty(){
		return amount;
	}
	
	@Override
	public void process(VisionSource source) {
		if(amount.get() <= 0)
			amount.set(1);
		source.largestContours(amount.get());
	}
}
