package edu.flash3388.flashlib.vision;

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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import edu.flash3388.flashlib.math.Mathd;

public class CvProcessing {

	private CvProcessing(){}
	
	public static class CvSource implements VisionSource{
		
		private Mat mat, threshold = new Mat(), contoursHierarchy = new Mat();
		private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		private Analysis analysis;
		private CvPipeline pipeline;

		private void checkReady(){
			if(mat == null)
				throw new VisionSourceImageMissingException();
		}
		
		public Mat getThreshold(){
			return threshold;
		}
		public List<MatOfPoint> getContours(){
			return contours;
		}
		
		public void setPipeline(CvPipeline pipe){
			pipeline = pipe;
		}
		public void prep(Mat mat){
			this.mat = mat;
			analysis = null;
		}
		
		private void detectContours(){
			if(pipeline != null)
				pipeline.newImage(threshold, CvPipeline.TYPE_THRESHOLD);
			contours.clear();
			CvProcessing.detectContours(threshold, contours, contoursHierarchy);
		}
		
		@Override
		public Analysis getResult() {
			if(analysis != null)
				return analysis;
			if(contours.size() != 1)
				return null;
			
			MatOfPoint contour = contours.get(0);
			analysis = new Analysis();
			CvProcessing.setAnalysisForContour(mat, contour, analysis);
			
			return analysis;
		}
		@Override
		public Analysis[] getResults() {
			Analysis[] ans = new Analysis[contours.size()];
			
			for (int i = 0; i < ans.length; i++) {
				ans[i] = new Analysis();
				CvProcessing.setAnalysisForContour(mat, contours.get(i), ans[i]);
			}
			return ans;
		}
		
		@Override
		public void filterHsv(int minH, int minS, int minV, int maxH, int maxS, int maxV) {
			checkReady();
			
			CvProcessing.rgbToHsv(mat, threshold);
			CvProcessing.filterMatColors(threshold, threshold, minH, maxH, minS, maxS, minV, maxV);
			detectContours();
		}
		@Override
		public void filterRgb(int minR, int minG, int minB, int maxR, int maxG, int maxB) {
			checkReady();
			
			CvProcessing.filterMatColors(mat, threshold, minR, maxR, minG, maxG, minB, maxB);
			detectContours();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> void filterByComparator(int amount, Comparator<T> comparator) {
			checkReady();
			
			CvProcessing.filterByComparator(contours, amount, (Comparator<MatOfPoint>) comparator);
		}
		@Override
		public void lowestContours(int amount) {
			checkReady();
			
			CvProcessing.filterForLowestContours(contours, amount);
		}
		@Override
		public void highestContours(int amount) {
			checkReady();
			
			CvProcessing.filterForHighestContours(contours, amount);
		}
		@Override
		public void largestContours(int amount) {
			checkReady();
			
			CvProcessing.filterForLargestContours(contours, amount);
		}

		@Override
		public void detectShapes(int amount, int vertecies, double accuracy) {
			checkReady();
			
			detectShapes(vertecies, accuracy);
			if(amount < contours.size()){
				for (int i = contours.size() - 1; i >= amount; i--)
					contours.remove(i);
			}
		}
		@Override
		public void detectShapes(int vertecies, double accuracy) {
			checkReady();
			
			CvProcessing.detectContoursByShape(contours, vertecies, accuracy);
		}

		@Override
		public void contourRatio(double heightRatio, double widthRatio, double dy, double dx, 
				double maxScore, double minScore, 
				double maxHeight, double minHeight, double maxWidth, double minWidth) {
			checkReady();
			
			analysis = CvProcessing.filterForContoursByRatio(contours, mat, heightRatio, widthRatio, dy, dx, 
					maxScore, minScore, maxHeight, minHeight, maxWidth, minWidth);
			if(analysis == null)
				contours.clear();
		}

		@Override
		public void closestToLeft(int amount) {
			checkReady();
			
			CvProcessing.filterForClosestToLeft(contours, mat, amount);
		}
		@Override
		public void closestToRight(int amount) {
			checkReady();
			
			CvProcessing.filterForClosestToRight(contours, mat, amount);
		}

		@Override
		public void closestToCoordinate(double x, double y, int amount) {
			checkReady();
			
			CvProcessing.filterForClosestContoursToPoint(contours, x, y, amount);
		}
		@Override
		public void closestToCenterFrame(int amount) {
			checkReady();
			
			CvProcessing.filterForClosestContoursToCenter(contours, mat, amount);
		}
	}
	
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	
	public static Mat rgbToHsv(Mat mat, Mat hsv){
		Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_RGB2HSV);
		return hsv;
	}
	public static Mat rgbToHsv(Mat mat){
		Mat hsv = new Mat();
		return rgbToHsv(mat, hsv);
	}
	
	public static Mat filterMatColors(Mat mat, Mat threshold, int min1, int max1, int min2, int max2, int min3, int max3){
		Core.inRange(mat, 
				new Scalar(min1, min2, min3), 
				new Scalar(max1, max2, max3), 
				threshold);
		return threshold;
	}
	public static Mat filterMatColors(Mat mat, int min1, int max1, int min2, int max2, int min3, int max3){
		Mat threshold = new Mat();
		return filterMatColors(mat, threshold, min1, max1, min2, max2, min3, max3);
	}
	
	public static List<MatOfPoint> detectContours(Mat threshold, List<MatOfPoint> contours, Mat hierarchy) {
		Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		return contours;
	}
	public static List<MatOfPoint> detectContours(Mat threshold){
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		
		return detectContours(threshold, contours, hierarchy);
	}
	
	public static void detectContoursByShape(List<MatOfPoint> contours, int vertecies, double accuracy){
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		
		for(int idx = contours.size() - 1; idx >= 0; idx--){
			MatOfPoint contour = contours.get(idx);
		   
		    matOfPoint2f.fromList(contour.toList());
		    Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * accuracy, true);
		    long total = approxCurve.total();
		    
		    if (total != vertecies)
		    	contours.remove(idx);
		}
	}
	
	public static void filterByComparator(List<MatOfPoint> contours, int amount, Comparator<MatOfPoint> comparator) {
		contours.sort(comparator);
		
		int size = amount < contours.size() && amount > 0? amount : contours.size();
		for (int i = contours.size() - 1; i >= size; i--)
			contours.remove(i);
	}
	
	public static void filterForLargestContours(List<MatOfPoint> contours, int amount){
		Comparator<MatOfPoint> sizeComparer = (MatOfPoint o1, MatOfPoint o2) ->{
			if(o1.total() < o2.total()) return 1;
			else if(o2.total() < o1.total()) return -1;
			return 0;
		};
		filterByComparator(contours, amount, sizeComparer);
	}
	public static void filterForSmallestContours(List<MatOfPoint> contours, int amount){
		Comparator<MatOfPoint> sizeComparer = (MatOfPoint o1, MatOfPoint o2) ->{
			if(o1.total() > o2.total()) return 1;
			else if(o2.total() > o1.total()) return -1;
			return 0;
		};
		filterByComparator(contours, amount, sizeComparer);
	}
	public static void filterForLowestContours(List<MatOfPoint> contours, int amount){
		Comparator<MatOfPoint> heightComparer = (MatOfPoint o1, MatOfPoint o2) ->{
			double y1 = contourCenter(o1).y, y2 = contourCenter(o2).y; 
			if(y1 < y2) return 1;
			else if(y2 < y1) return -1;
			return 0;
		};
		filterByComparator(contours, amount, heightComparer);
	}
	public static void filterForHighestContours(List<MatOfPoint> contours, int amount){
		Comparator<MatOfPoint> heightComparer = (MatOfPoint o1, MatOfPoint o2) ->{
			double y1 = contourCenter(o1).y, y2 = contourCenter(o2).y;
			if(y1 > y2) return 1;
			else if(y2 > y1) return -1;
			return 0;
		};
		filterByComparator(contours, amount, heightComparer);
	}
	public static void filterForClosestContoursToPoint(List<MatOfPoint> contours, double x, double y, int amount) {
		Comparator<MatOfPoint> comparator = (MatOfPoint o1, MatOfPoint o2) ->{
			Point p1 = CvProcessing.contourCenter(o1), p2 = CvProcessing.contourCenter(o2);
			double d1 = Mathd.pythagorasTheorem(p1.x - x, p1.y - y),
				   d2 = Mathd.pythagorasTheorem(p2.x - x, p2.y - y);
			if(d1 > d2) return 1;
			else if(d2 > d1) return -1;
			return 0;
		};
		filterByComparator(contours, amount, comparator);
	}
	public static void filterForClosestContoursToCenter(List<MatOfPoint> contours, Mat feed, int amount) {
		filterForClosestContoursToPoint(contours, feed.width() * 0.5, feed.height() * 0.5, amount);
	}
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
	private static void setAnalysisForRatio(List<MatOfPoint> contours, Rect[] rects, int i1, int i2, 
			Analysis an, Mat mat){
		Rect r = getRectFromArray(rects, contours.get(i1), i1);
		Rect r2 = getRectFromArray(rects, contours.get(i2), i2);
		
		drawRect(mat, r, new Scalar(21, 21, 21));
		drawRect(mat, r2, new Scalar(71, 71, 71));
		
		an.centerPointX = r.x+r.width+(r2.x - (r.x + r.width)) / 2.0;
		an.centerPointY = r.y + r.height + (r2.y - (r.y + r.height)) / 2.0;
		an.verticalDistance = (int) (an.centerPointY - mat.height() * 0.5);
		an.horizontalDistance = (int) (an.centerPointX - mat.width() * 0.5);
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
				System.out.println("<SIZE> ->> H: "+r.height+" W: "+r.width);
				continue;
			}
			
			for(int j = 0; j < contours.size() && !foundBest; j++){
				if(i == j)
					continue;
				c2 = contours.get(j);
				Rect r2 = getRectFromArray(rects, c2, j);
				
				if(impossibleSize(r2, maxHeight, minHeight, maxWidth, minWidth)){
					System.out.println("<SIZE> ->> H: "+r2.height+" W: "+r2.width);
					continue;
				}
				
				double avg = getAvgForRatio(r, r2, heightRatio, widthRatio, dy, dx);
				if(avg < best){
					best = avg;
					bestIdx1 = i;
					bestIdx2 = j;
					
					System.out.println("<POSSIBILITY> ->> H: "+r.height+" / "+r2.height+" || W: "+r.width+" / "+r2.width+
							" || A: "+avg);
					
					if(best < minScore)
						foundBest = true;
				}
			}
			if(best < minScore)
				break;
		}
		if(best > maxScore || bestIdx1 < 0 || bestIdx2 < 0){
			return null;
		}
		
		Analysis an = new Analysis();
		setAnalysisForRatio(contours, rects, bestIdx1, bestIdx2, an, feed);
		System.out.println();
		return an;
	}
	
	//------------------------------------------------------------------
	//---------------------Contour Drawing------------------------------
	//------------------------------------------------------------------
	
	
	public static void drawContours(Mat feed, List<MatOfPoint> contours, Scalar color) {
		for(int i = 0; i < contours.size(); i++)
			Imgproc.drawContours(feed, contours, i, color);
	}
	public static void drawContours(Mat feed, List<MatOfPoint> contours, int r, int g, int b) {
		drawContours(feed, contours, new Scalar(r, g, b));
	}
	public static void drawContours(Mat feed, List<MatOfPoint> contours) {
		drawContours(feed, contours, new Scalar(51, 51, 51));
	}
	public static void drawContours(Mat feed, Scalar color, MatOfPoint...contours) {
		drawContours(feed, Arrays.asList(contours), color);
	}
	public static void drawContours(Mat feed, int r, int g, int b, MatOfPoint...contours) {
		drawContours(feed, new Scalar(r, g, b), contours);
	}
	public static void drawContours(Mat feed, MatOfPoint...contours) {
		drawContours(feed, new Scalar(51, 51, 51), contours);
	}
	public static void drawRect(Mat feed, Rect r, Scalar color){
		Imgproc.rectangle(feed, r.tl(), r.br(), color);
	}
	public static void drawPostProcessing(Mat feed, Scalar color, Analysis... an){
		for (int i = 0; i < an.length; i++) {
			Analysis analysis = an[i];
			Imgproc.circle(feed, new Point(analysis.centerPointX, analysis.centerPointY), 3, color, 2);
		}
		Imgproc.line(feed, new Point(feed.width()/2, 0), new Point(feed.width()/2, feed.height()), new Scalar(0, 51, 255));
	}
	public static void drawPostProcessing(Mat feed, Analysis... an){
		drawPostProcessing(feed, new Scalar(51,255, 51), an);
	}
	
	//------------------------------------------------------------------
	//---------------------------Util-----------------------------------
	//------------------------------------------------------------------

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
	public static Point contourCenter2(MatOfPoint contour) {
		Rect rect = Imgproc.boundingRect(contour);
		return contourCenter2(rect);
	}
	public static Point contourCenter2(Rect rect) {
		return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
	}
	
	public static Mat bufferedImage2Mat(BufferedImage image){
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  		  
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);  		  
		mat.put(0, 0, data);  		  		  
		return mat;  
	}
	public static Mat byteArray2Mat(byte[] data){
		return Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}
	
	public static void setAnalysisForContour(Mat feed, MatOfPoint contour, Analysis analysis){
		Point center = contourCenter(contour);
		
		analysis.centerPointX = center.x;
		analysis.centerPointY = center.y;
		analysis.verticalDistance = (int) (center.y - feed.height() * 0.5);
		analysis.horizontalDistance = (int) (center.x - feed.width() * 0.5);
	}
	public static List<List<Point>> contoursToPointList(List<MatOfPoint> contours) {
		List<List<Point>> returnStruct = new ArrayList<List<Point>>();
		for(int i = 0; i < contours.size(); i++){
			returnStruct.add(contours.get(i).toList());
		}
		return returnStruct;
	}
}
