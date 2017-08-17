package edu.flash3388.flashlib.vision;

public class MatchResult {
	public double centerx, centery;
	public double scaleFactor;
	public double maxVal;
	
	public MatchResult(double centerx, double centery, double scaleFactor,double maxVal) {
		this.centerx = centerx;
		this.centery = centery;
		this.scaleFactor = scaleFactor;
		this.maxVal = maxVal;
	}
}
