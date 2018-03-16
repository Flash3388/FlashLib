package edu.flash3388.flashlib.vision;

/**
 * Filers out contours by their proximity to the left side of the frame. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#closestToLeft(int)
 */
public class ClosestToLeftFilter extends VisionFilter{
	
	/**
	 * Indicates the maximum amount of contours to leave after the filter process.
	 * Must be non-negative.
	 */
	private int amount;

	public ClosestToLeftFilter(){}
	public ClosestToLeftFilter(int amount){
		this.amount = amount;
	}
	
	@Override
	public void process(VisionSource source) {
		source.closestToLeft(amount);
	}
}
