package edu.flash3388.flashlib.vision;


/**
 * Filers out contours by their proximity to the upper edge of the frame.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#highestContours(int)
 */
public class HighestFilter implements VisionFilter{
	
	/**
	 * Indicates the maximum amount of contours to leave after the filter process.
	 * Must be non-negative,
	 */
	private int amount;
	
	public HighestFilter(){}
	public HighestFilter(int amount){
		this.amount = amount;
	}
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	@Override
	public void process(VisionSource source) {
		source.highestContours(amount);
	}
}
