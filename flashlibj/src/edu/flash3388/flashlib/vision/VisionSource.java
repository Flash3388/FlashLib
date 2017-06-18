package edu.flash3388.flashlib.vision;

import java.util.Comparator;

/**
 * Vision source provides a base for vision operations. To use a library for vision processing, it needs to implement this
 * class and than can be used by the vision system. 
 * <p>
 * Each method in a vision source performs a different filtering operation which filters out contours. That operation 
 * can be used filters.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionProcessing
 */
public interface VisionSource {

	/**
	 * Gets the result of the filtering. This is done by checking the amount of contours left. If only one remains, it is
	 * returned. Otherwise null is returned.
	 * @return the analysis of the vision
	 */
	Analysis getResult();
	/**
	 * Gets the result of the filtering. This is done by creating an analysis for each remaining contour and return them
	 * as an array. 
	 * @return the analysis of the vision
	 */
	Analysis[] getResults();
	
	/**
	 * Filters the image for HSV values. Pixels with color within the given parameters are regarded as contours.
	 * 
	 * @param minH min hue
	 * @param minS min saturation
	 * @param minV min value
	 * @param maxH max hue
	 * @param maxS max saturation
	 * @param maxV max value
	 */
	void filterHsv(int minH, int minS, int minV,int maxH, int maxS, int maxV);
	/**
	 * Filters the image for RGB values. Pixels with color within the given parameters are regarded as contours.
	 * 
	 * @param minR min red
	 * @param minG min green
	 * @param minB min blue
	 * @param maxR max red
	 * @param maxG max green
	 * @param maxB max blue
	 */
	void filterRgb(int minR, int minG, int minB,int maxR, int maxG, int maxB);
	
	/**
	 * Sorts the contours by a comparator and removes contours for the bottom of the list if the list contains
	 * too many contours. Can be used to filter contours by size, position, shape and more.
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @param comparator the comparator for sorting the list of contours
	 * @param <T> comparator type
	 */
	<T> void filterByComparator(int amount, Comparator<T> comparator);
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 */
	void highestContours(int amount);
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 */
	void lowestContours(int amount);
	/**
	 * Sorts the contours by their size and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 */
	void largestContours(int amount);
	
	/**
	 * Approximates the shape of each contour with a given accuracy and checks if that shape matches the wanted one by
	 * the amount of vertices. If it does not, the contour is removed. At the end, if too many shapes remain, than they 
	 * are removed as well.
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @param vertecies the amount of vertices of the shape
	 * @param accuracy the accuracy of approximation
	 */
	void detectShapes(int amount, int vertecies, double accuracy);
	/**
	 * Approximates the shape of each contour with a given accuracy and checks if that shape matches the wanted one by
	 * the amount of vertices. If it does not, the contour is removed. 
	 * 
	 * @param vertecies the amount of vertices of the shape
	 * @param accuracy the accuracy of approximation
	 */
	void detectShapes(int vertecies, double accuracy);
	
	/**
	 * Searches for 2 contours with certain size ratios and size restrictions and filters out all remaining contours.
	 * <p>
	 * Ratio filtering is done by checking the ratio of sizes between the contours and comparing it to the desired result.
	 * The best ratio is achieved when the difference between the actual result and the desired result is minimal.
	 * </p>
	 * <p>
	 * Size restriction is done by checking the size of each contour. If the contour does not meet the requirements it is
	 * ignored.
	 * </p>
	 * <p>
	 * Scoring is between 0 and 1. where 0 is the best possible result. If a score is better than the min score it is automatically
	 * used. If the result contours' score is bigger than the max score, they are ignored.
	 * </p>
	 * 
	 * @param heightRatio the height ration between the two contours
	 * @param widthRatio the width ration between the two contours
	 * @param dy the expected ratio between the height of the first contour and the total height of both contours and the distance
	 * between them
	 * @param dx the expected ratio between the width of the first contour and the total width of both contours and the distance
	 * between them
	 * @param maxScore the worst possible score
	 * @param minScore the best possible score
	 * @param maxHeight max height restriction
	 * @param minHeight min height restriction
	 * @param maxWidth max width restriction
	 * @param minWidth min width restriction
	 */
	void contourRatio(double heightRatio, double widthRatio, double dy, double dx, double maxScore, double minScore, 
			double maxHeight, double minHeight, double maxWidth, double minWidth);
	
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 */
	void closestToLeft(int amount);
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 */
	void closestToRight(int amount);
	
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	void closestToCoordinate(double x, double y, int amount);
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param amount the maximum amount of contours that should remain after the operation
	 */
	void closestToCenterFrame(int amount);
}
