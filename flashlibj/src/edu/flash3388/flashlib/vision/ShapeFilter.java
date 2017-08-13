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
	
	private DoubleProperty accuracy = new SimpleDoubleProperty();
	private IntegerProperty amount = new SimpleIntegerProperty();
	private IntegerProperty vertecies = new SimpleIntegerProperty();

	public ShapeFilter(){}
	public ShapeFilter(int amount, int vertecies, double accuracy){
		this.amount.set(amount);
		this.vertecies.set(vertecies);
		this.accuracy.set(accuracy);
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
	/**
	 * An {@link IntegerProperty}.
	 * Indicates the amount of vertices and the requested shape to find.
	 * Must be non-negative
	 * @return the property
	 */
	public IntegerProperty verteciesProperty(){
		return vertecies;
	}
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the accuracy of the shape approximation. Higher values offer less accurate shapes.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty accuracyProperty(){
		return accuracy;
	}
	
	@Override
	public void process(VisionSource source) {
		if(amount.get() <= 0)
			source.detectShapes(vertecies.get(), accuracy.get());
		else
			source.detectShapes(amount.get(), vertecies.get(), accuracy.get());
	}
}
