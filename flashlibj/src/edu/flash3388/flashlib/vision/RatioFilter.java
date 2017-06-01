package edu.flash3388.flashlib.vision;

public class RatioFilter extends ProcessingFilter{
	private double heightRatio, widthRatio, dy, dx, maxScore, minScore, 
			maxHeight, minHeight, maxWidth, minWidth;

	public RatioFilter(){}
	public RatioFilter(double heightRatio, double widthRatio, double dy, double dx, double maxScore, double minScore, 
			double maxHeight, double minHeight, double maxWidth, double minWidth){
		this.maxScore = maxScore;
		this.dx = dx;
		this.dy = dy; 
		this.heightRatio = heightRatio;
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.minScore = minScore;
		this.minWidth = minWidth;
		this.widthRatio = widthRatio;
	}
	
	public double getHeightRatio(){
		return heightRatio;
	}
	public void setHeightRatio(double heightRatio){
		this.heightRatio = heightRatio;
	}
	public double getWidthRatio(){
		return widthRatio;
	}
	public void setWidthRatio(double widthRatio){
		this.widthRatio = widthRatio;
	}
	public double getXRatio(){
		return dx;
	}
	public void setXRatio(double dx){
		this.dx = dx;
	}
	public double getYRatio(){
		return dy;
	}
	public void setYRatio(double dy){
		this.dy = dy;
	}
	
	public double getMaxScore(){
		return maxScore;
	}
	public void setMaxScore(double maxScore){
		this.maxScore = maxScore;
	}
	public double getMinScore(){
		return minScore;
	}
	public void setMinScore(double minScore){
		this.minScore = minScore;
	}
	
	public double getMaxHeight(){
		return maxHeight;
	}
	public void setMaxHeight(double maxHeight){
		this.maxHeight = maxHeight;
	}
	public double getMinHeight(){
		return minHeight;
	}
	public void setMinHeight(double minHeight){
		this.minHeight = minHeight;
	}
	public double getMaxWidth(){
		return maxWidth;
	}
	public void setMaxWidth(double maxWidth){
		this.maxWidth = maxWidth;
	}
	public double getMinWidth(){
		return minWidth;
	}
	public void setMinWidth(double minWidth){
		this.minWidth = minWidth;
	}
	
	@Override
	public void process(VisionSource source) {
		source.contourRatio(heightRatio, widthRatio, dy, dx, maxScore, minScore, 
				maxHeight, minHeight, maxWidth, minWidth);
	}
	@Override
	public void parseParameters(double[] parameters) {
		if(parameters.length != 10)
			throw new IllegalArgumentException("Arguments invalid! Require 10");
		
		heightRatio = parameters[0];
		widthRatio = parameters[1];
		dy = parameters[2];
		dx = parameters[3];
		maxScore = parameters[4];
		minScore = parameters[5];
		maxHeight = parameters[6];
		minHeight = parameters[7];
		maxWidth = parameters[8];
		minWidth = parameters[9];
	}
	@Override
	public double[] getParameters() {
		return new double[] {heightRatio, widthRatio, dy, dx, maxScore, minScore, maxHeight, minHeight,
				maxWidth, minWidth};
	}
}
