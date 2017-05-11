package edu.flash3388.flashlib.vision;

import java.awt.image.BufferedImage;

public interface VisionSource {

	Analysis analyse(ProcessingFilter[] filter, BufferedImage img);
	
	void filterHsv(int minH, int minS, int minV,int maxH, int maxS, int maxV);
	void filterRgb(int minR, int minG, int minB,int maxR, int maxG, int maxB);
	
	void highestContours(int amount);
	void largestContours(int amount);
	
	void detectShapes(int amount, int vertecies, double accuracy);
	void detectShapes(int vertecies, double accuracy);
	
	void dimensionsRatio(double height, double width);
	void positionRatio(double dy, double dx);
	
	void highest();
	void lowest();
	void closestToLeft();
	void closestToRight();
	void closestToCooredinate(double x, double y);
	void closestToCenterY();
	void closestToCenterX();
}
