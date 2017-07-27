package edu.flash3388.flashlib.vision;

import java.util.Map;


/**
 * Filers out contours by their shape.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#detectShapes(int, int, double)
 */
public class ShapeFilter extends ProcessingFilter{
	private byte amount, vertecies;
	private double accuracy;

	public ShapeFilter(){}
	public ShapeFilter(int amount, int vertecies, double accuracy){
		this.amount = (byte) amount;
		this.vertecies = (byte) vertecies;
		this.accuracy = accuracy;
	}
	
	public int getAmount(){
		return amount;
	}
	public void setAmount(int amount){
		this.amount = (byte) amount;
	}
	public int getVertecies(){
		return vertecies;
	}
	public void setVertecies(int vertecies){
		this.vertecies = (byte) vertecies;
	}
	public double getAccuracy(){
		return accuracy;
	}
	public void setAccuracy(double accuracy){
		this.accuracy = accuracy;
	}
	
	@Override
	public void process(VisionSource source) {
		if(amount <= 0)
			source.detectShapes(vertecies, accuracy);
		else
			source.detectShapes(amount, vertecies, accuracy);
	}
	@Override
	public void parseParameters(Map<String, VisionParam> parameters) {
		amount = (byte) VisionParam.getIntValue(parameters.get("amount"));
		vertecies = (byte) VisionParam.getIntValue(parameters.get("vertecies"));
		accuracy = VisionParam.getDoubleValue(parameters.get("accuracy"));
	}
	@Override
	public VisionParam[] getParameters() {
		return new VisionParam[]{
				new VisionParam.IntParam("amount", amount),
				new VisionParam.IntParam("vertecies", vertecies),
				new VisionParam.DoubleParam("accuracy", accuracy)
		};
	}
}
