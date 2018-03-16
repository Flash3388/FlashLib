package edu.flash3388.flashlib.vision;


/**
 * Filers out contours by their proximity to a the lower edge of the frame.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#lowestContours(int)
 */
public class LowestFilter implements VisionFilter{

	/**
	 * Indicates the maximum amount of contours to leave after the filter process.
	 * Must be non-negative,
	 */
	private int amount;
	
	public LowestFilter(){}
	public LowestFilter(int amount){
		this.amount = amount;
	}
	
	@Override
	public void process(VisionSource source) {
		source.lowestContours(amount);
	}
}
