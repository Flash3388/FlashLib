package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;

/**
 * Filers out contours by their proximity to the center of the frame. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#closestToCenterFrame(int)
 */
public class ClosestToCenterFilter extends VisionFilter{
	
	private IntegerProperty amount = new SimpleIntegerProperty();
	
	public ClosestToCenterFilter(){}
	public ClosestToCenterFilter(int amount){
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
		source.closestToCenterFrame(amount.get());
	}
}
