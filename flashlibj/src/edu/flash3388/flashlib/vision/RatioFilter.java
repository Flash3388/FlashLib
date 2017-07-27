package edu.flash3388.flashlib.vision;

import java.util.Map;


/**
 * Filers out contours using ratio filtering. Designed to locate to contours with specific position and side ratios.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#contourRatio(double, double, double, double, double, double, double, double, double, double)
 */
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
	public void parseParameters(Map<String, VisionParam> parameters) {
		heightRatio = VisionParam.getDoubleValue(parameters.get("heightRatio"));
		widthRatio = VisionParam.getDoubleValue(parameters.get("widthRatio"));
		dx = VisionParam.getDoubleValue(parameters.get("dx"));
		dy = VisionParam.getDoubleValue(parameters.get("dy"));
		minScore = VisionParam.getDoubleValue(parameters.get("minScore"));
		maxScore = VisionParam.getDoubleValue(parameters.get("maxScore"));
		minHeight = VisionParam.getDoubleValue(parameters.get("minHeight"));
		maxHeight = VisionParam.getDoubleValue(parameters.get("maxHeight"));
		minWidth = VisionParam.getDoubleValue(parameters.get("minWidth"));
		maxWidth = VisionParam.getDoubleValue(parameters.get("maxWidth"));
	}
	@Override
	public VisionParam[] getParameters() {
		return new VisionParam[]{
				new VisionParam.DoubleParam("heightRatio", heightRatio),
				new VisionParam.DoubleParam("widthRatio", widthRatio),
				new VisionParam.DoubleParam("dx", dx),
				new VisionParam.DoubleParam("dy", dy),
				new VisionParam.DoubleParam("minScore", minScore),
				new VisionParam.DoubleParam("maxScore", maxScore),
				new VisionParam.DoubleParam("minHeight", minHeight),
				new VisionParam.DoubleParam("maxHeight", maxHeight),
				new VisionParam.DoubleParam("minWidth", minWidth),
				new VisionParam.DoubleParam("maxWidth", maxWidth)
		};
	}
}
