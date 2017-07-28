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
	
	public IntegerProperty amountProperty(){
		return amount;
	}
	public IntegerProperty verteciesProperty(){
		return vertecies;
	}
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
