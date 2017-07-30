package edu.flash3388.flashlib.vision;

import java.util.Comparator;
import java.util.List;

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
	 * Gets the width of the current used frame. If no frame exists, returns 0.
	 * @return the width in pixels of the current frame, or 0 if no frame exists.
	 */
	int getFrameWidth();
	/**
	 * Gets the height of the current used frame. If no frame exists, returns 0.
	 * @return the height in pixels of the current frame, or 0 if no frame exists.
	 */
	int getFrameHeight();
	/**
	 * Gets the contours available in the frame. Uses a contour wrapper class to contain contour data
	 * from the used implementation.
	 * @return list of contours on the frame.
	 */
	List<Contour> getContours();
	
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
	 * Converts the image to a HSV format.
	 */
	void convertHsv();
	/**
	 * Converts the image to a RGB format.
	 */
	void convertRgb();
	/**
	 * Converts the image to a gray scale format.
	 */
	void convertGrayscale();
	
	/**
	 * 
	 * Filters image for data in a given color range. All data in range is considered a contour.
	 * Used only for RGB or HSV images.
	 * 
	 * @param min1 minimum first value. Red for RGB, Hue for HSV.
	 * @param min2 minimum second value. Green for RGB, Saturation for HSV.
	 * @param min3 minimum third value. Blue for RGB, Value for HSV.
	 * @param max1 maximum first value. Red for RGB, Hue for HSV.
	 * @param max2 maximum second value. Green for RGB, Saturation for HSV.
	 * @param max3 maximum third value. Blue for RGB, Value for HSV.
	 */
	void filterColorRange(int min1, int min2, int min3, int max1, int max2, int max3);
	/**
	 * 
	 * Filters image for data in a given color range. All data in range is considered a contour.
	 * Used only for grayscale images.
	 * 
	 * @param min minimum value.
	 * @param max maximum value.
	 */
	void filterColorRange(int min, int max);
	
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
	 * Filters the image for any circles. Any non-circle contours are filtered out.
	 */
	void circleDetection();
	
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
	
	void  templateMatch();
	
	
}

/*
 * 
	/**
	 * Gets an analysis object for a given list of contours. 
	 * @param contours list of contours
	 * @return an {@link Analysis} object for the given contours
	 */
	//Analysis getAnalysisForContours(List<Contour> contours);

