package edu.flash3388.flashlib.vision;

import java.util.Map;

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
	public void parseParameters(Map<String, FilterParam> parameters) {
		amount = (byte) FilterParam.getIntValue(parameters.get("amount"));
		vertecies = (byte) FilterParam.getIntValue(parameters.get("vertecies"));
		accuracy = FilterParam.getDoubleValue(parameters.get("accuracy"));
	}
	@Override
	public FilterParam[] getParameters() {
		return new FilterParam[]{
				new FilterParam.IntParam("amount", amount),
				new FilterParam.IntParam("vertecies", vertecies),
				new FilterParam.DoubleParam("accuracy", accuracy)
		};
	}
}
