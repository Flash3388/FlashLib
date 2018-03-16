package edu.flash3388.flashlib.vision;


/**
 * Filers out contours by their size.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#largestContours(int)
 */
public class LargestFilter extends VisionFilter{
	
	/**
	 * Indicates the maximum amount of contours to leave after the filter process.
	 * Must be non-negative,
	 */
	private int amount;

	public LargestFilter(){}
	public LargestFilter(int amount){
		this.amount = amount;
	}
	
	@Override
	public void process(VisionSource source) {
		if(amount <= 0)
			amount = 1;
		source.largestContours(amount);
	}
}
