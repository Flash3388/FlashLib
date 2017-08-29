package edu.flash3388.flashlib.vision.cv;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Contour;
import edu.flash3388.flashlib.vision.MatchResult;
import edu.flash3388.flashlib.vision.cv.CvTemplateMatcher.Method;

/**
 * Provides openCV utilities and vision functionalities.
 * 
 * @author Tom Tzook
 * @author Alon Klein
 * @since FlashLib 1.0.0
 */
public class CvProcessing {

	private CvProcessing(){}
	
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	
	/**
	 * Converts a mat from RGB to HSV.
	 * @param mat an RGB mat
	 * @param hsv a mat to fill with HSV data
	 * @return the hsv mat 
	 * @see Imgproc#cvtColor(Mat, Mat, int)
	 */
	public static Mat rgbToHsv(Mat mat, Mat hsv){
		Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_RGB2HSV);
		return hsv;
	}
	/**
	 * Converts an RGB mat to HSV.
	 * @param mat an RGB mat
	 * @return an HSV mat 
	 * @see Imgproc#cvtColor(Mat, Mat, int)
	 */
	public static Mat rgbToHsv(Mat mat){
		Mat hsv = new Mat();
		return rgbToHsv(mat, hsv);
	}
	/**
	 * Converts a mat to gray.
	 * @param mat a mat to convert
	 * @param gray a mat to fill with gray data
	 * @return the gray mat 
	 * @see Imgproc#cvtColor(Mat, Mat, int)
	 */
	public static Mat rgbToGray(Mat mat, Mat gray){
		if(mat.type() == CvType.CV_8UC1)
			mat.copyTo(gray);
		else if(mat.type() == CvType.CV_8UC3)
			Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
		return gray;
	}
	/**
	 * Converts an RGB mat to gray.
	 * @param mat an RGB mat
	 * @return the gray mat 
	 * @see Imgproc#cvtColor(Mat, Mat, int)
	 */
	public static Mat rgbToGray(Mat mat){
		Mat gray = new Mat();
		return rgbToGray(mat, gray);
	}
	
	/**
	 * Filters mat data by colors. Data within the color boundary now represents a contour.  
	 * 
	 * @param mat the mat data
	 * @param threshold the resulting binary image
	 * @param min1 min boundary 1: min hue/red
	 * @param max1 max boundary 1: max hue/red
	 * @param min2 min boundary 2: min saturation/green
	 * @param max2 max boundary 2: max saturation/green
	 * @param min3 min boundary 3: min value/blue
	 * @param max3 max boundary 3: max value/blue
	 * @return the binary image
	 * @see Core#inRange(Mat, Scalar, Scalar, Mat)
	 */
	public static Mat filterMatColors(Mat mat, Mat threshold, int min1, int max1, int min2, int max2, int min3, int max3){
		Core.inRange(mat, 
				new Scalar(min1, min2, min3), 
				new Scalar(max1, max2, max3), 
				threshold);
		return threshold;
	}
	/**
	 * Filters mat data by colors. Data within the color boundary now represents a contour.  
	 * 
	 * @param mat the mat data
	 * @param min1 min boundary 1: min hue/red
	 * @param max1 max boundary 1: max hue/red
	 * @param min2 min boundary 2: min saturation/green
	 * @param max2 max boundary 2: max saturation/green
	 * @param min3 min boundary 3: min value/blue
	 * @param max3 max boundary 3: max value/blue
	 * @return a binary image
	 * @see Core#inRange(Mat, Scalar, Scalar, Mat)
	 */
	public static Mat filterMatColors(Mat mat, int min1, int max1, int min2, int max2, int min3, int max3){
		Mat threshold = new Mat();
		return filterMatColors(mat, threshold, min1, max1, min2, max2, min3, max3);
	}
	
	/**
	 * Detects contours within a binary image.
	 * 
	 * @param threshold the binary image
	 * @param contours list of contours to fill
	 * @param hierarchy hierarchy of contours
	 * @return the contours param
	 * @see Imgproc#findContours(Mat, List, Mat, int, int)
	 */
	public static List<MatOfPoint> detectContours(Mat threshold, List<MatOfPoint> contours, Mat hierarchy) {
		Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		return contours;
	}
	/**
	 * Detects contours within a binary image.
	 * 
	 * @param threshold the binary image
	 * @return the contours list
	 * @see Imgproc#findContours(Mat, List, Mat, int, int)
	 */
	public static List<MatOfPoint> detectContours(Mat threshold){
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		
		return detectContours(threshold, contours, hierarchy);
	}
	
	/**
	 * Filters contours by shape. Iterates through the list of contours and approximates their shape. 
	 * Compares the vertices of the shape to the desired vertices and removes the contour if they do not match.
	 * 
	 * @param contours list of contours
	 * @param vertices vertices of the desired shape
	 * @param accuracy the accuracy of approximation
	 * @see Imgproc#approxPolyDP(MatOfPoint2f, MatOfPoint2f, double, boolean)
	 */
	public static void detectContoursByShape(List<MatOfPoint> contours, int vertices, double accuracy){
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		
		for(int idx = contours.size() - 1; idx >= 0; idx--){
			MatOfPoint contour = contours.get(idx);
		   
		    matOfPoint2f.fromList(contour.toList());
		    Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * accuracy, true);
		    long total = approxCurve.total();
		    
		    if (total != vertices)
		    	contours.remove(idx);
		}
	}
	

	/**
	 * removes all contours which not found as circles
	 * 
	 * @param threshold mat
	 * @param contours of contours
	 * 
	 */
	public static void FilterByCircle(Mat threshold,List<MatOfPoint> contours) {
	    Mat circles = DetectCircle(threshold,10);
	    //MatOfPoint mpoints = new MatOfPoint(threshold);
	   // MatOfPoint2f threshold2f = new MatOfPoint2f(mpoints.toArray());
	    
		System.out.println("circles Found - " + circles.rows());
	    for (int i = 0; i < contours.size(); i++){
			
			boolean foundMatch = false;
			for (int j = 0; j < circles.rows();j++){	

				double[] data = circles.get(j, 0);
				Point center = new Point(data[0],data[1]);
				
				double distance = Imgproc.pointPolygonTest(new MatOfPoint2f(contours.get(i)) , center, true);
				if(distance < 0)//inside
				{
					foundMatch = true; //found matching circle for contour 
					break;
				}
					
			}
			if(!foundMatch)
				contours.remove(i);
		}	
		
	}

	
	/**
	 * Detects circles in a binary image.
	 * 
	 * @param threshold The thresholded mat
	 * @param distance min distance between circles
	 * @return matrix of detected circles
	 */
	public static Mat DetectCircle(Mat threshold,int distance) {

	    Mat circles = new Mat();
	    Imgproc.HoughCircles(threshold, circles, Imgproc.CV_HOUGH_GRADIENT,1,distance);

	    return circles;
	}
	
	/**
	 * Sorts the contours by a comparator and removes contours for the bottom of the list if the list contains
	 * too many contours. Can be used to filter contours by size, position, shape and more.
	 * 
	 * @param contours list of contours
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @param comparator the comparator for sorting the list of contours
	 */
	public static void filterByComparator(List<MatOfPoint> contours, int amount, Comparator<MatOfPoint> comparator) {
		contours.sort(comparator);
		
		int size = amount < contours.size() && amount > 0? amount : contours.size();
		for (int i = contours.size() - 1; i >= size; i--)
			contours.remove(i);
	}
	/**
	 * Sorts the contours by their size and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param contours list of contours
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @see #filterByComparator(List, int, Comparator)
	 */
	public static void filterForLargestContours(List<MatOfPoint> contours, int amount){
		Comparator<MatOfPoint> sizeComparer = (MatOfPoint o1, MatOfPoint o2) ->{
			if(o1.total() < o2.total()) return 1;
			else if(o2.total() < o1.total()) return -1;
			return 0;
		};
		filterByComparator(contours, amount, sizeComparer);
	}
	/**
	 * Sorts the contours by their size and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param contours list of contours
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @see #filterByComparator(List, int, Comparator)
	 */
	public static void filterForSmallestContours(List<MatOfPoint> contours, int amount){
		Comparator<MatOfPoint> sizeComparer = (MatOfPoint o1, MatOfPoint o2) ->{
			if(o1.total() > o2.total()) return 1;
			else if(o2.total() > o1.total()) return -1;
			return 0;
		};
		filterByComparator(contours, amount, sizeComparer);
	}
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param contours list of contours
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @see #filterByComparator(List, int, Comparator)
	 */
	public static void filterForLowestContours(List<MatOfPoint> contours, int amount){
		Comparator<MatOfPoint> heightComparer = (MatOfPoint o1, MatOfPoint o2) ->{
			double y1 = contourCenter(o1).y, y2 = contourCenter(o2).y; 
			if(y1 < y2) return 1;
			else if(y2 < y1) return -1;
			return 0;
		};
		filterByComparator(contours, amount, heightComparer);
	}
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param contours list of contours
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @see #filterByComparator(List, int, Comparator)
	 */
	public static void filterForHighestContours(List<MatOfPoint> contours, int amount){
		Comparator<MatOfPoint> heightComparer = (MatOfPoint o1, MatOfPoint o2) ->{
			double y1 = contourCenter(o1).y, y2 = contourCenter(o2).y;
			if(y1 > y2) return 1;
			else if(y2 > y1) return -1;
			return 0;
		};
		filterByComparator(contours, amount, heightComparer);
	}
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param contours list of contours
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @param x x coordinate on the frame
	 * @param y y coordinate on the frame
	 * @see #filterByComparator(List, int, Comparator)
	 */
	public static void filterForClosestContoursToPoint(List<MatOfPoint> contours, double x, double y, int amount) {
		Comparator<MatOfPoint> comparator = (MatOfPoint o1, MatOfPoint o2) ->{
			Point p1 = CvProcessing.contourCenter(o1), p2 = CvProcessing.contourCenter(o2);
			double d1 = Mathf.pythagorasTheorem(p1.x - x, p1.y - y),
				   d2 = Mathf.pythagorasTheorem(p2.x - x, p2.y - y);
			if(d1 > d2) return 1;
			else if(d2 > d1) return -1;
			return 0;
		};
		filterByComparator(contours, amount, comparator);
	}
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param contours list of contours
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @param feed the frame
	 * @see #filterByComparator(List, int, Comparator)
	 */
	public static void filterForClosestContoursToCenter(List<MatOfPoint> contours, Mat feed, int amount) {
		filterForClosestContoursToPoint(contours, feed.width() * 0.5, feed.height() * 0.5, amount);
	}
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param contours list of contours
	 * @param feed the frame
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @see #filterByComparator(List, int, Comparator)
	 */
	public static void filterForClosestToRight(List<MatOfPoint> contours, Mat feed, int amount){
		Comparator<MatOfPoint> comparator = (MatOfPoint o1, MatOfPoint o2) ->{
			Point p1 = CvProcessing.contourCenter(o1), p2 = CvProcessing.contourCenter(o2);
			double d1 = Math.abs(feed.width() - p1.x),
				   d2 = Math.abs(feed.width() - p2.x);
			if(d1 > d2) return 1;
			else if(d2 > d1) return -1;
			return 0;
		};
		filterByComparator(contours, amount, comparator);
	}
	/**
	 * Sorts the contours by their position and removes contours for the bottom of the list if the list contains
	 * too many contours. 
	 * 
	 * @param contours list of contours
	 * @param amount the maximum amount of contours that should remain after the operation
	 * @param feed the frame
	 * @see #filterByComparator(List, int, Comparator)
	 */
	public static void filterForClosestToLeft(List<MatOfPoint> contours, Mat feed, int amount){
		Comparator<MatOfPoint> comparator = (MatOfPoint o1, MatOfPoint o2) ->{
			Point p1 = CvProcessing.contourCenter(o1), p2 = CvProcessing.contourCenter(o2);
			double d1 = p1.x,
				   d2 = p2.x;
			if(d1 > d2) return 1;
			else if(d2 > d1) return -1;
			return 0;
		};
		filterByComparator(contours, amount, comparator);
	}
	
	//------------------------------------------------------------------
	//---------------------Contour Ratio--------------------------------
	//------------------------------------------------------------------
	
	private static Rect getRectFromArray(Rect[] rects, MatOfPoint contour, int i){
		Rect r = rects[i] != null ? rects[i] : Imgproc.boundingRect(contour);
		if(rects[i] == null) 
			rects[i] = r;
		return r;
	}
	private static boolean impossibleSize(Rect rect, double maxHeight, double minHeight, double maxWidth, double minWidth){
		return rect.height > maxHeight || rect.height < minHeight || rect.width > maxWidth || rect.width < minWidth;
	}
	private static void setAnalysisForRatio(MatOfPoint contour1, MatOfPoint contour2, Rect[] rects, int i1, int i2, 
			Analysis an, Mat mat){
		Rect r = getRectFromArray(rects, contour1, i1);
		Rect r2 = getRectFromArray(rects, contour2, i2);
		
		drawRect(mat, r, new Scalar(21, 21, 21));
		drawRect(mat, r2, new Scalar(71, 71, 71));
	
		double centerx = (r.x+r.width+(r2.x - (r.x + r.width)) / 2.0);
		double centery = (r.y + r.height + (r2.y - (r.y + r.height)) / 2.0);
		
		an.setDouble(Analysis.PROP_CENTER_X, centerx);
		an.setDouble(Analysis.PROP_CENTER_Y, centery);
		an.setDouble(Analysis.PROP_VERTICAL_DISTANCE, (centery - mat.height() * 0.5));
		an.setDouble(Analysis.PROP_HORIZONTAL_DISTANCE, (centerx - mat.width() * 0.5));
	}
	private static double getAvgForRatio(Rect r, Rect r2, double heightRatio, double widthRatio, double dy, double dx){
		int count = 0;
		double sum = 0;
		
		if(heightRatio > 0){
			count++;
			sum += Math.abs(heightRatio - ((double)r.height / r2.height));
		}
		if(widthRatio > 0){
			count++;
			sum += Math.abs(widthRatio - ((double)r.width / r2.width));
		}
		if(dy != 0){
			count++;
			sum += Math.abs(dy - ((double)(r2.y + r2.height - r.y) / (r.height)));
		}
		if(dx != 0){
			count++;
			sum += Math.abs(dx - ((double)(r2.x + r2.width - r.x) / (r.width)));
		}
		
		return sum / count;
	}
	
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
	 * <p>
	 * The given list is emptied if no result is found. If the contours are found, they are the only remaining contours
	 * in the list.
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
	 * @param feed the frame
	 * @param contours list of contours
	 * 
	 * @return the result of the ratio filtering. An analysis for both contours if found, or null otherwise
	 */
	public static Analysis filterForContoursByRatio(List<MatOfPoint> contours, Mat feed, 
			double heightRatio, double widthRatio, double dy, double dx, 
			double maxScore, double minScore, 
			double maxHeight, double minHeight, double maxWidth, double minWidth) {
		Rect[] rects = new Rect[contours.size()];
		MatOfPoint c1 = null, c2 = null;
		boolean foundBest = false;
		double best = 1;
		int bestIdx1 = -1, bestIdx2 = -1;
		
		for(int i = contours.size() - 1; i >= 0 && !foundBest; i--){
			c1 = contours.get(i);
		
			Rect r = getRectFromArray(rects, c1, i);
			
			if(impossibleSize(r, maxHeight, minHeight, maxWidth, minWidth)){
				continue;
			}
			
			for(int j = 0; j < contours.size() && !foundBest; j++){
				if(i == j)
					continue;
				c2 = contours.get(j);
				Rect r2 = getRectFromArray(rects, c2, j);
				
				if(impossibleSize(r2, maxHeight, minHeight, maxWidth, minWidth)){
					continue;
				}
				
				double avg = getAvgForRatio(r, r2, heightRatio, widthRatio, dy, dx);
				if(avg < best){
					best = avg;
					bestIdx1 = i;
					bestIdx2 = j;
					
					if(best < minScore)
						foundBest = true;
				}
			}
			if(best < minScore)
				break;
		}
		if(best > maxScore || bestIdx1 < 0 || bestIdx2 < 0){
			contours.clear();
			return null;
		}
		
		MatOfPoint contour1 = contours.get(bestIdx1), contour2 = contours.get(bestIdx2);
		contours.clear();
		contours.add(contour1);
		contours.add(contour2);
		
		Analysis an = new Analysis();
		setAnalysisForRatio(contour1, contour2, rects, bestIdx1, bestIdx2, an, feed);
		return an;
	}
	
	//------------------------------------------------------------------
	//---------------------Contour Drawing------------------------------
	//------------------------------------------------------------------
	
	/**
	 * Draws a list of contours unto a mat.
	 * 
	 * @param feed the mat to draw on
	 * @param contours list of contours to draw
	 * @param color color to draw with
	 * @see Imgproc#drawContours(Mat, List, int, Scalar)
	 */
	public static void drawContours(Mat feed, List<MatOfPoint> contours, Scalar color) {
		for(int i = 0; i < contours.size(); i++)
			Imgproc.drawContours(feed, contours, i, color);
	}
	/**
	 * Draws a list of contours unto a mat.
	 * 
	 * @param feed the mat to draw on
	 * @param contours list of contours to draw
	 * @param r red value for color
	 * @param g green value for color
	 * @param b blue value for color
	 * @see Imgproc#drawContours(Mat, List, int, Scalar)
	 */
	public static void drawContours(Mat feed, List<MatOfPoint> contours, int r, int g, int b) {
		drawContours(feed, contours, new Scalar(r, g, b));
	}
	/**
	 * Draws a list of contours unto a mat. Draws with RGB color (51, 51, 51)
	 * 
	 * @param feed the mat to draw on
	 * @param contours list of contours to draw
	 * @see Imgproc#drawContours(Mat, List, int, Scalar)
	 */
	public static void drawContours(Mat feed, List<MatOfPoint> contours) {
		drawContours(feed, contours, new Scalar(51, 51, 51));
	}
	/**
	 * Draws an array of contours unto a mat.
	 * 
	 * @param feed the mat to draw on
	 * @param contours array of contours to draw
	 * @param color color to draw with
	 * @see Imgproc#drawContours(Mat, List, int, Scalar)
	 */
	public static void drawContours(Mat feed, Scalar color, MatOfPoint...contours) {
		drawContours(feed, Arrays.asList(contours), color);
	}
	/**
	 * Draws an array of contours unto a mat.
	 * 
	 * @param feed the mat to draw on
	 * @param contours array of contours to draw
	 * @param r red value for color
	 * @param g green value for color
	 * @param b blue value for color
	 * @see Imgproc#drawContours(Mat, List, int, Scalar)
	 */
	public static void drawContours(Mat feed, int r, int g, int b, MatOfPoint...contours) {
		drawContours(feed, new Scalar(r, g, b), contours);
	}
	/**
	 * Draws an array of contours unto a mat. With an RGB color of (51, 51, 51).
	 * 
	 * @param feed the mat to draw on
	 * @param contours array of contours to draw
	 * @see Imgproc#drawContours(Mat, List, int, Scalar)
	 */
	public static void drawContours(Mat feed, MatOfPoint...contours) {
		drawContours(feed, new Scalar(51, 51, 51), contours);
	}
	/**
	 * Draws a {@link Rect} object unto a mat.
	 * 
	 * @param feed the mat to draw on
	 * @param r object to draw
	 * @param color color to draw with
	 * @see Imgproc#rectangle(Mat, Point, Point, Scalar)
	 */
	public static void drawRect(Mat feed, Rect r, Scalar color){
		Imgproc.rectangle(feed, r.tl(), r.br(), color);
	}
	/**
	 * Draws an array of analysis objects unto a mat.
	 * 
	 * @param feed mat to draw on
	 * @param color color to draw with
	 * @param an array of analysis to draw
	 * @see Imgproc#circle(Mat, Point, int, Scalar)
	 */
	public static void drawPostProcessing(Mat feed, Scalar color, Analysis... an){
		for (int i = 0; i < an.length; i++) {
			Analysis analysis = an[i];
			Imgproc.circle(feed, 
					new Point(analysis.getDouble(Analysis.PROP_CENTER_X), analysis.getDouble(Analysis.PROP_CENTER_Y)), 
					3, color, 2);
		}
		Imgproc.line(feed, new Point(feed.width()/2, 0), new Point(feed.width()/2, feed.height()), new Scalar(0, 51, 255));
	}
	/**
	 * Draws an array of analysis objects unto a mat. Draws with an RGB value of (51, 51, 51)
	 * 
	 * @param feed mat to draw on
	 * @param an array of analysis to draw
	 * @see Imgproc#circle(Mat, Point, int, Scalar)
	 */
	public static void drawPostProcessing(Mat feed, Analysis... an){
		drawPostProcessing(feed, new Scalar(51,255, 51), an);
	}
	
	//------------------------------------------------------------------
	//---------------------------Util-----------------------------------
	//------------------------------------------------------------------

	/**
	 * Gets the center point of a contour by summing the x and y coordinates of the points contained within it and
	 * dividing it by the amount of points.
	 * 
	 * @param contour the contour
	 * @return the center point of a contour
	 */
	public static Point contourCenter(MatOfPoint contour) {
		List<Point> pointArr;
		pointArr = contour.toList();
		int size = pointArr.size();
		Point currPoint;
		int sumX = 0;
		int sumY = 0;
		for(int y = 0; y < size; y++){
			currPoint = pointArr.get(y);
			sumX += (int)currPoint.x;
			sumY += (int)currPoint.y;					
		}
		
		return(new Point((int)sumX/size,(int)sumY/size));
	}
	/**
	 * Gets the center point of a contour by creating a bounding rectangle and getting its center.
	 * 
	 * @param contour the contour
	 * @return the center point of a contour
	 */
	public static Point contourCenter2(MatOfPoint contour) {
		Rect rect = Imgproc.boundingRect(contour);
		return contourCenter2(rect);
	}
	/**
	 * Gets the center point of a rectangle by getting its x + half width and y + half height
	 * 
	 * @param rect the rectangle
	 * @return the center point of a contour
	 */
	public static Point contourCenter2(Rect rect) {
		return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
	}
	
	/**
	 * Converts a buffered image object in to an openCV mat.
	 * @param image buffered image
	 * @return mat
	 */
	public static Mat bufferedImage2Mat(BufferedImage image){
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  		  
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);  		  
		mat.put(0, 0, data);  		  		  
		return mat;  
	}
	/**
	 * Converts a byte array into a mat object
	 * @param data byte array
	 * @return mat
	 */
	public static Mat byteArray2Mat(byte[] data){
		return Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}
	
	/**
	 * Sets the values of an analysis object for a contour.
	 * 
	 * @param feed the image mat
	 * @param contour the contour
	 * @param analysis the analysis object
	 */
	public static void setAnalysisForContour(Mat feed, MatOfPoint contour, Analysis analysis){
		Point center = contourCenter(contour);
		setAnalysisForCenter(feed,center,analysis);
	}
	
	/**
	 * Sets the values of an analysis object for a center.
	 * 
	 * @param feed the image mat
	 * @param center the center of target as Point
	 * @param analysis the analysis object
	 */
	public static void setAnalysisForCenter(Mat feed, Point center, Analysis analysis){
		analysis.setDouble(Analysis.PROP_CENTER_X, center.x);
		analysis.setDouble(Analysis.PROP_CENTER_Y, center.y);
		analysis.setDouble(Analysis.PROP_VERTICAL_DISTANCE, (center.y - feed.height() * 0.5));
		analysis.setDouble(Analysis.PROP_HORIZONTAL_DISTANCE, (center.x - feed.width() * 0.5));
	}
	
	/**
	 * Saves data from an opencv {@link MatOfPoint} into a {@link Contour} object
	 * to simplify data for user.
	 * 
	 * @param contour opencv contour
	 * @return a contour wrapper
	 */
	public static Contour wrapCvContour(MatOfPoint contour){
		Rect rect = Imgproc.boundingRect(contour);
		Point center = contourCenter2(rect);
		return new Contour((int)center.x, (int)center.y, rect.width, rect.height);
	}
	
	/**
	 * Converts a list of contours into a list of a list of points. That is since a contour is a collection of points.
	 * 
	 * @param contours the list of contours
	 * @return list of list of points
	 */
	public static List<List<Point>> contoursToPointList(List<MatOfPoint> contours) {
		List<List<Point>> returnStruct = new ArrayList<List<Point>>();
		for(int i = 0; i < contours.size(); i++){
			returnStruct.add(contours.get(i).toList());
		}
		return returnStruct;
	}
	
	/**
	 * Resizes the given image by a given factor. If the scale factor is positive, the image is enlarged, otherwise
	 * it's size is decreased.
	 * 
	 * @param img the image to resize
	 * @param scaleFactor the size factor in pixels
	 */
	public static void resize(Mat img, double scaleFactor){
		Imgproc.resize(img, img, new Size(0,0),scaleFactor,scaleFactor,Imgproc.INTER_CUBIC);		
	}
	/**
	 * Searches for a given template in a given image and returns the best part of the image which matches the result.
	 * 
	 * @param scene the full image to search
	 * @param templ the template image
	 * @param method the method of template matching
	 * @param scaleFactor resizing factor in pixels
	 * @return a result of the match
	 */
	public static MatchResult matchTemplate(Mat scene, Mat templ, Method method, double scaleFactor){
		return CvProcessing.matchTemplate(scene, (new Mat[]{templ}), method, scaleFactor);
	}
	
	public static MatchResult matchTemplate(Mat scene, Mat[] templ, Method method, double scaleFactor){
		return CvTemplateMatcher.match(scene, templ, method, scaleFactor);
	}
}
