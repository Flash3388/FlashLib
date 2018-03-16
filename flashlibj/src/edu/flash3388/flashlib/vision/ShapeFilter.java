package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;


/**
 * Filers out contours by their shape.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#detectShapes(int, int, double)
 */
public class ShapeFilter extends VisionFilter{
	
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
	
	
	@Override
	public void process(VisionSource source) {
		if(amount <= 0)
			source.detectShapes(vertecies, accuracy);
		else
			source.detectShapes(amount, vertecies, accuracy);
	}
}
