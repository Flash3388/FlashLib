package edu.flash3388.flashlib.vision;
/**
 * Filers out contours by their proximity to a coordinate in the frame.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#closestToCoordinate(double, double, int)
 */
public class CoordinateFilter implements VisionFilter{
	
	/**
	 * Indicates the x coordinate to filter around.
	 */
	private double x;
	/**
	 * Indicates the y coordinate to filter around.
	 */
	private double y;
	/**
	 * Indicates the maximum amount of contours to leave after the filter process.
	 * Must be non-negative.
	 */
	private int amount;

	public CoordinateFilter(){}
	public CoordinateFilter(double x, double y, int amount){
		this.x = x;
		this.y = y;
		this.amount = amount;
	}
	
	@Override
	public void process(VisionSource source) {
		source.closestToCoordinate(x, y, amount);
	}
}
