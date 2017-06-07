package edu.flash3388.flashlib.vision;

import java.util.Map;

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
	public void parseParameters(Map<String, FilterParam> parameters) {
		heightRatio = FilterParam.getDoubleValue(parameters.get("heightRatio"));
		widthRatio = FilterParam.getDoubleValue(parameters.get("widthRatio"));
		dx = FilterParam.getDoubleValue(parameters.get("dx"));
		dy = FilterParam.getDoubleValue(parameters.get("dy"));
		minScore = FilterParam.getDoubleValue(parameters.get("minScore"));
		maxScore = FilterParam.getDoubleValue(parameters.get("maxScore"));
		minHeight = FilterParam.getDoubleValue(parameters.get("minHeight"));
		maxHeight = FilterParam.getDoubleValue(parameters.get("maxHeight"));
		minWidth = FilterParam.getDoubleValue(parameters.get("minWidth"));
		maxWidth = FilterParam.getDoubleValue(parameters.get("maxWidth"));
	}
	@Override
	public FilterParam[] getParameters() {
		return new FilterParam[]{
				new FilterParam.DoubleParam("heightRatio", heightRatio),
				new FilterParam.DoubleParam("widthRatio", widthRatio),
				new FilterParam.DoubleParam("dx", dx),
				new FilterParam.DoubleParam("dy", dy),
				new FilterParam.DoubleParam("minScore", minScore),
				new FilterParam.DoubleParam("maxScore", maxScore),
				new FilterParam.DoubleParam("minHeight", minHeight),
				new FilterParam.DoubleParam("maxHeight", maxHeight),
				new FilterParam.DoubleParam("minWidth", minWidth),
				new FilterParam.DoubleParam("maxWidth", maxWidth)
		};
	}
}
