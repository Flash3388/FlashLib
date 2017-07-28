package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;

/**
 * Filers out contours by their proximity to the left side of the frame. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#closestToLeft(int)
 */
public class ClosestToLeftFilter extends VisionFilter{
	
	private IntegerProperty amount = new SimpleIntegerProperty();

	public ClosestToLeftFilter(){}
	public ClosestToLeftFilter(int amount){
		this.amount.set(amount);
	}
	
	public IntegerProperty amountProperty(){
		return amount;
	}
	
	@Override
	public void process(VisionSource source) {
		source.closestToLeft(amount.get());
	}
}
