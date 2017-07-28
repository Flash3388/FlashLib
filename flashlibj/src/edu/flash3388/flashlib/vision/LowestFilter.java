package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;


/**
 * Filers out contours by their proximity to a the lower edge of the frame.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#lowestContours(int)
 */
public class LowestFilter extends VisionFilter{

	private IntegerProperty amount = new SimpleIntegerProperty();
	
	public LowestFilter(){}
	public LowestFilter(int amount){
		this.amount.set(amount);
	}
	
	public IntegerProperty amountProperty(){
		return amount;
	}
	
	@Override
	public void process(VisionSource source) {
		source.lowestContours(amount.get());
	}
}
