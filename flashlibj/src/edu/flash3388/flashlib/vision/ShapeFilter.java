package edu.flash3388.flashlib.vision;

/**
 * Filers out contours by their shape.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#detectShapes(int, int, double)
 */
public class ShapeFilter implements VisionFilter {
	
	/**
	 * Indicates the accuracy of the shape approximation. Higher values offer less accurate shapes.
	 * Must be non-negative.
	 */
	private double accuracy;
	/**
	 * Indicates the amount of vertices and the requested shape to find.
	 * Must be non-negative.
	 */
	private int vertecies;
	/**
	 * Indicates the maximum amount of contours to leave after the filter process.
	 * Must be non-negative.
	 */
	private int amount;
	
	public ShapeFilter(){}
	public ShapeFilter(int amount, int vertecies, double accuracy){
		this.amount = amount;
		this.vertecies = vertecies;
		this.accuracy = accuracy;
	}
	
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	public int getVertecies() {
		return vertecies;
	}
	public void setVertecies(int vertecies) {
		this.vertecies = vertecies;
	}
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	@Override
	public void process(VisionSource source) {
		if(amount <= 0)
			source.detectShapes(vertecies, accuracy);
		else
			source.detectShapes(amount, vertecies, accuracy);
	}
}
