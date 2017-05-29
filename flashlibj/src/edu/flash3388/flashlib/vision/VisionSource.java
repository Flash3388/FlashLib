package edu.flash3388.flashlib.vision;

import java.util.Comparator;

public interface VisionSource {

	Analysis getResult();
	Analysis[] getResults();
	
	void filterHsv(int minH, int minS, int minV,int maxH, int maxS, int maxV);
	void filterRgb(int minR, int minG, int minB,int maxR, int maxG, int maxB);
	
	<T> void filterByComparator(int amount, Comparator<T> comparator);
	void highestContours(int amount);
	void lowestContours(int amount);
	void largestContours(int amount);
	
	void detectShapes(int amount, int vertecies, double accuracy);
	void detectShapes(int vertecies, double accuracy);
	
	void contourRatio(double heightRatio, double widthRatio, double dy, double dx, double maxScore, double minScore, 
			double maxHeight, double minHeight, double maxWidth, double minWidth);
	
	void closestToLeft(int amount);
	void closestToRight(int amount);
	
	void closestToCoordinate(double x, double y, int amount);
	void closestToCenterFrame(int amount);
}
