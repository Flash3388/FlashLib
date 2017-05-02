package edu.flash3388.flashlib.vision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CvProcessing {

	private CvProcessing(){}
	//------------------------------------------------------------------
	//-----------------------Helper Classes-----------------------------
	//------------------------------------------------------------------
	
	private static class Scores {
		public Point center;
		public double distanceToTarget;
	}
		
	//------------------------------------------------------------------
	//-----------------------Class Variables----------------------------
	//------------------------------------------------------------------
		
	public static ImagePipeline pipeline;
	
	private static Mat thresholdMat = new Mat();
	private static int leastAccuracy = 3;
		
	private static Point lastCenter = new Point(0,0);
	private static Mat feed;
	private static Mat temp = new Mat();
	private static Comparator<MatOfPoint> comparatorLargest = (MatOfPoint o1, MatOfPoint o2)->{
		long s1 = o1.total();
		long s2 = o2.total();
		if(s1 < s2) 
			return 1;
		if(s2 < s1) 
			return -1;
		return 0;
	};
	
	//------------------------------------------------------------------
	//-----------------------Main Methods-------------------------------
	//------------------------------------------------------------------
	
	public static Analysis analyseImage(Mat mat, ProcessingParam param) {
		return ImageAnalyzer.AnalyzeImage(mat, param);
	}
	
	// This class handles parameters 
	
	public static class ImageAnalyzer {
		private ImageAnalyzer(){}
		public static Analysis AnalyzeImage(Mat mat, ProcessingParam param) {
			if(mat == null || param == null) 
				return null;

			checkIfRangeParametersAreCorrect(param);
			feed = mat;
			setupImageForBinaryFormat(mat, param);
			
			if(pipeline != null)
				pipeline.newImage(thresholdMat, ImagePipeline.TYPE_THRESHOLD);
			
			if(param.morphOps)
				morphOps(thresholdMat);
			
			List<MatOfPoint> contours = detectContours(thresholdMat);
			if(contours.size() < 1){ 
				if(pipeline != null)
					pipeline.newImage(feed, ImagePipeline.TYPE_POST_PROCESS);
				return null;
			}
			
			return detectObject(param, contours);
		}
		
		private static void checkIfRangeParametersAreCorrect(ProcessingParam param) {
			if(param.hue_red == null || param.sat_green == null || param.val_blue == null)
				throw new NullPointerException("Invalid Range - range not fully set (null)");
			
			//if(param.objHeight <= 0 || param.objWidth <= 0)
			//	throw new IllegalArgumentException("Invalid object dimensions - must be positive");
			if(param.hue_red.start < 0 || param.hue_red.end > 255 ||
				param.sat_green.start < 0 || param.sat_green.end > 255 ||
				param.val_blue.start < 0 || param.val_blue.end > 255)
				throw new IllegalArgumentException("Invalid detection range");
		}
		
		private static void setupImageForBinaryFormat(Mat mat, ProcessingParam param) {
			/*if(param.blur != 0)
				Imgproc.blur(mat, mat, new Size(param.blur, param.blur));*/
			
			if(param.hsv)
				Imgproc.cvtColor(mat, temp, Imgproc.COLOR_RGB2HSV);
			else mat.copyTo(temp);
			
			Core.inRange(temp, 
					new Scalar(param.hue_red.start, param.sat_green.start, param.val_blue.start), 
					new Scalar(param.hue_red.end, param.sat_green.end, param.val_blue.end), 
					thresholdMat);
		}
		
		private static Analysis detectObject(ProcessingParam param, List<MatOfPoint> contours) {
			//final double HEIGHT_DISTANCE_TO_CAMERA = 15.5;
			//final double HALF_ROBOT_IN_CM = 6.7;
			Analysis ret = getCenterOfTarget(contours, param);
			
			if(ret == null){
				if(pipeline != null)
					pipeline.newImage(feed, ImagePipeline.TYPE_POST_PROCESS);
				return null;
			}
			
			Point p = new Point(ret.centerPointX, ret.centerPointY);
			offsetsFromLast(p, lastCenter);
			
			Imgproc.circle(feed, p, 3, new Scalar(51,255, 51), 2);
			Imgproc.line(feed, new Point(feed.width()/2, 0), new Point(feed.width()/2, feed.height()), new Scalar(0, 51, 255));
			lastCenter = p;
			
			ret.horizontalDistance = (int) (p.x - (feed.width()/2));
			ret.verticalDistance = (int) (p.y - (feed.height()/2));
			//ret.offsetAngle = measureVerticalAngle(ret.targetDistance, ret.pixelsToCmRatio, HEIGHT_DISTANCE_TO_CAMERA,
			//		ret.horizontalDistance, HALF_ROBOT_IN_CM);//first of all, i dont know how to work with it so how do i commit? second, i want EVERYTHING on git
			
			if(pipeline != null)
				pipeline.newImage(feed, ImagePipeline.TYPE_POST_PROCESS);
			
			ret.centerPointX = p.x;
			ret.centerPointY = p.y;
			return ret;
		}
	}
	
	private static Analysis getCenterOfTarget(List<MatOfPoint> contours, ProcessingParam params) {
		switch (params.mode){
			case Both:    return null;
			case Highest: return highestMode(contours, params);
			case Largest: return largestMode(contours, params);
			case Rect:    return rectMode(contours, params);
			case Ratio:   return ratioMode(contours, params);
		}
		return null;
	}
	
	private static void offsetsFromLast(Point center, Point last) {
		if(center.x - last.x <= leastAccuracy && center.x - last.x >= -leastAccuracy)
			center.x = last.x;	
		if(center.y - last.y <= leastAccuracy && center.y - last.y >= -leastAccuracy)
			center.y = last.y;
	}
	
	//------------------------------------------------------------------
	//-----------------------Vision Modes-------------------------------
	//------------------------------------------------------------------
	
	private static Analysis highestMode(List<MatOfPoint> contours, ProcessingParam params) {
		Analysis an = new Analysis();
		
		List<MatOfPoint> largestContours = largestContours(contours);
		int[] indexes = highestContour(largestContours);

		Point p = contourCenter(largestContours.get(0));
		an.centerPointX = p.x;
		an.centerPointY = p.y;
		
		return an;
	}
	
	private static Analysis largestMode(List<MatOfPoint> contours, ProcessingParam params) {
		Analysis an = new Analysis();
		
		List<MatOfPoint> largestContours = largestContours(contours);
		Point p = contourCenter(largestContours.get(0));
		an.centerPointX = p.x;
		an.centerPointY = p.y;
		
		return an;
	}

	private static Analysis rectMode(List<MatOfPoint> contours, ProcessingParam params) {
		Analysis an = new Analysis();
		
		List<MatOfPoint> largestContours = largestContours(contours);
		List<MatOfPoint> rects = detectRectangles(largestContours);
		
		Point p = null;
		if(rects == null)
			p = contourCenter(largestContours.get(0));
		else
			p = closestToY(rects);
		an.centerPointX = p.x;
		an.centerPointY = p.y;
		return an;
	}
	
	private static Analysis ratioMode(List<MatOfPoint> contours, ProcessingParam params) {
		if(contours.size() < 2) 
			return null;
		
		Scores bestScore = scoreHandler(thresholdMat, contours, params.targetBoiler);
		if(bestScore == null)
			return null;
		
		Analysis an = new Analysis();
		Point p = bestScore.center;
		an.centerPointX = p.x;
		an.centerPointY = p.y;
		an.targetDistance = bestScore.distanceToTarget;
		return an;
	}
	
	//------------------------------------------------------------------
	//------------------------Morph Ops---------------------------------
	//------------------------------------------------------------------
	
	private static void morphOps(Mat threshold) {
		Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
		Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
		
		Imgproc.erode(threshold, threshold, erode); 
		Imgproc.erode(threshold, threshold, erode);
		
		Imgproc.dilate(threshold, threshold, dilate);
		Imgproc.dilate(threshold, threshold, dilate);
	}
	
	//------------------------------------------------------------------
	//---------------------Contour Locating-----------------------------
	//------------------------------------------------------------------
	
	private static List<MatOfPoint> detectContours(Mat threshold) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		
		Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		return contours;
	}
	
	//returns the couple largest contours 
	private static List<MatOfPoint> largestContours(List<MatOfPoint> contours) {
		List<MatOfPoint> outputContours = new ArrayList<MatOfPoint>(); 
		contours.sort(new Comparator<MatOfPoint>() {
			@Override
			public int compare(MatOfPoint o1, MatOfPoint o2) {
				int s1 = o1.toList().size();
				int s2 = o2.toList().size();
				if(s1 < s2) 
					return 1;
				if(s2 < s1) 
					return -1;
				return 0;
			}
		});
		
		for(int i = 0; i < 5; i++){
			if(contours.size() < i + 1) break;
			outputContours.add(contours.get(i));
		}
		return outputContours;
	}
	
	private static int[] highestContour(List<MatOfPoint> contours) {
		final int INDEX_ARRAY_SIZE = 3;
		int max1 = Integer.MIN_VALUE;
		int max2 = Integer.MIN_VALUE;
		int max3 = Integer.MIN_VALUE;  //assuming integer elements in the array
		
		int[] indexs = new int[INDEX_ARRAY_SIZE];
		MatOfPoint currContour;
		
		for (int i = 0; i < contours.size(); i++) {
			currContour = contours.get(i);
			int y= (int)contourCenter(currContour).y;
			
		    if (y > max1)
		    {
		        max3 = max2; 
		        max2 = max1; 
		        max1 = y;
		        indexs[2] = indexs[1]; 
		        indexs[1] = indexs[0]; 
		        indexs[0] = i;
		    } else if (y> max2) {
		        max3 = max2;
		        max2 = y;
		        indexs[2]= indexs[1]; 
		        indexs[1]= i;
		    }else if (y> max3) {
		        max3 = y;
		        indexs[2] = i;
		    }
		}
		
		return indexs;
	}
	
	private static List<MatOfPoint> detectRectangles(List<MatOfPoint> contours) {
		List<MatOfPoint> ret = new ArrayList<MatOfPoint>();
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		
		for(int idx = 0; idx < contours.size();idx++){
			MatOfPoint contour = contours.get(idx);
		   
		    matOfPoint2f.fromList(contour.toList());
		    final double ACCURACY_OF_DP = 0.02;
		    Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * ACCURACY_OF_DP, true);
		    long total = approxCurve.total();
		    
		    final int FOUR_SIDES = 4;
		    final int FIVE_SIDES = 5;
		    if (total >= FOUR_SIDES && total <= FIVE_SIDES) {
		    	drawContours(new Scalar(25, 255, 25), contour);
		    	ret.add(contour);
		    }
		}
		
		if(ret.size()<1)
			return null;
		return ret;
	}
	
	private static Point closestToY(List<MatOfPoint> contours) {
		Point currPoint;
		Point ret = null;
		int height = feed.height()/2;
		
		int currY;
		int closestY = Integer.MAX_VALUE;
		
		for(MatOfPoint curr : contours)
		{
			currPoint = contourCenter(curr);
			currY = (int)currPoint.y;
			currY = Math.abs(currY - height);
			
			if(currY < closestY)
			{
				closestY = currY;
				ret = currPoint;
			}
			
		}
		return ret;
	}
	
	//------------------------------------------------------------------
	//---------------------Contour Points-------------------------------
	//------------------------------------------------------------------
	
	//returns a center of a sigle contur
	private static Point contourCenter(MatOfPoint contour) {
		List<Point> pointArr;
		pointArr = contour.toList();
		int size = pointArr.size();
		Point currPoint;
		int sumX = 0;
		int sumY = 0;
		for( int y = 0; y<size; y++){
			currPoint = pointArr.get(y);
			sumX += (int)currPoint.x;
			sumY += (int)currPoint.y;					
		}
		
		return(new Point((int)sumX/size,(int)sumY/size));
	}
	
	private static Point contourCenter2(MatOfPoint contour) {
		Rect rect = Imgproc.boundingRect(contour);
		return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
	}
	
	private static Point contourCenter2(Rect rect) {
		return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
	}

	//------------------------------------------------------------------
	//---------------------Scores Functions-----------------------------
	//------------------------------------------------------------------
	
	public static class ScoreHandler {
		
		static final int MINIMUM_CONTOUR_WIDTH = 10;
		static final int MINIMUM_CONTOUR_HEIGHT= 5;
		static final double REAL_WIDTH_LIFT = 6.0;
		static final double REAL_WIDTH_BOILER = 8.0;
		static final double BEST_RATIO_OF_SCORE = 90;
		static final double MINIMUM_SCORE = 70;
		double targetHeight; 
		Rect[] rects;
		Scores best = null;
		double bestavg = 0;
		MatOfPoint c1 = null;
		MatOfPoint c2 = null;
		
		
		public Scores getBestScore(Mat threshold, List<MatOfPoint> contours,boolean target){
			final double INCHES_TO_CENTIMETER = 2.54;
			final double BOILER_HEIGHT = 10 * INCHES_TO_CENTIMETER, LIFT_HEIGHT = 6.5 * INCHES_TO_CENTIMETER; 
			Scores best = null;
			rects = new Rect[contours.size()];
			boolean foundBest = false;
			bestavg = 0;
			targetHeight = target ? BOILER_HEIGHT : LIFT_HEIGHT;
			for(int i = 0; i < contours.size() && !foundBest; i++){
				c1 = contours.get(i);
			
				Rect r = currRectFromArray(c1, i);
				
				if(impossibleRectProperties(target, r))
					continue;
				
				for(int j = 0; j < contours.size() && !foundBest; j++){
					if(i == j)
						continue;
					c2 = contours.get(j);
					Rect r2 = currRectFromArray(c2, j);
					if(impossibleRectProperties(target, r2))
						continue;
					double avg = target? averageScoreBoiler(r, r2) : averageScoreLift(r, r2);
					if(bestAverage(bestavg, avg))
					{
						best = scoreMembersSetter(r, r2, avg);
						System.out.println("avg --> " + avg);
						if(avg > BEST_RATIO_OF_SCORE)
						{
							//BestScoreDrawer(r, r2);
							foundBest = true;
						}
					}
				}
				if(bestavg > BEST_RATIO_OF_SCORE)
					break;
			}
			if(best == null || bestavg < MINIMUM_SCORE){
				System.out.println("NOT FOUND!");
				return null;
			}
			System.out.println("Score: "+bestavg);
			System.out.println("Distance: "+best.distanceToTarget+" cm");
			return best;
		}

		private void BestScoreDrawer(Rect r, Rect r2) {
			Imgproc.rectangle(feed, r.tl(), r.br(), new Scalar(51, 51, 51));
			Imgproc.rectangle(feed, r2.tl(), r2.br(), new Scalar(51, 51, 51));
			System.out.println("W:"+r.width);
			System.out.println("Y1:"+r.y+" Y2:"+r2.y);
		}

		private Rect currRectFromArray(MatOfPoint c1, int i) {
			Rect r = rects[i] != null ? rects[i] : Imgproc.boundingRect(c1);
			if(rects[i] == null) rects[i] = r;
			return r;
		}

		private boolean impossibleRectPropertiesBoiler(Rect r2) {
			return r2.width < MINIMUM_CONTOUR_WIDTH || r2.height > r2.width;
		}
		private boolean impossibleRectProperties(boolean target, Rect r2){
			return target? (impossibleRectPropertiesBoiler(r2)) : (impossibleRectPropertiesLift(r2));
		}
		private boolean impossibleRectPropertiesLift(Rect r2) {
			return r2.height< MINIMUM_CONTOUR_HEIGHT || r2.width> r2.height ;
		}

		private Scores scoreMembersSetter(Rect r, Rect r2, double avg) {
			Scores best;
			best = new Scores();
			best.center = new Point(r.x+r.width+(r2.x - (r.x + r.width))/2, r.y + r.height + (r2.y - (r.y + r.height)) / 2);
			bestavg = avg;
			best.distanceToTarget = measureDistance(r.tl(), r2.br(), 19.14278582, targetHeight);
			
			//best.PixelsToCmRatio = r2.height/targetHeight;
			return best;
		}

		public static boolean bestAverage(double bestavg, double avg){
			return Math.abs(100 - bestavg) > Math.abs(100 - avg);
		}
		
		private static double averageScoreBoiler(Rect r, Rect r2){
			double rh = heightRatio(r.height, r2.height);
			double rw = widthRatio(r.width, r2.width);
			double gh = groupHeight(r.tl(), r2.br(), r.height);
			double dt = dTop(r.tl(), r2.tl(), r2.br());
			double le = lEdge(r.tl(), r2.tl(), r.width);
			return avg(rh, rw, gh, dt, le);
		}
		
		public static double ratioToScore(double ratio) {
			return 100 - (100*Math.abs(1-ratio));
			//return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
		}
		
		private static double lEdge(Point l1, Point l2, double width){
			return ratioToScore(((l1.x - l2.x) / width) + 1);
		}
		
		private static double groupHeight(Point t1,Point b2, double height1) {
			double lengthBetween = b2.y - t1.y;
			final double CALCULATED_RATIO = 10/25.0;
			return ratioToScore( height1/(lengthBetween * CALCULATED_RATIO));
		}
		
		public static double dTop(Point t1,Point t2,Point b2) {
			final double CALCULATED_RATIO =20/25.0;
			return ratioToScore( (t2.y-t1.y)/((b2.y-t1.y) * CALCULATED_RATIO));
		}
		
		private static double widthRatio(double w1,double w2) {
			return ratioToScore(w1/w2);
		}
		
		private static double heightRatio(double h1,double h2) {
			final double CALCULATED_RATIO = 2;
			return ratioToScore( h1/(CALCULATED_RATIO*h2));
		}
		
		
		// --------------
		//Lift Stuff
		//------------
		
		
		private static double averageScoreLift(Rect r, Rect r2){
			double rh = heightRatioLift(r.height, r2.height);
			double rw = widthRatio(r.width, r2.width);
			double gh = groupWidthLift(r.tl(), r.br(), r2.tl(), r2.br());
			double dt = dTopLift(r.tl(), r2.tl(), r2.br());
			double le = lEdgeLift(r.tl(), r2.tl(), r.height);
			return avg(rh, rw, gh, dt, le);
		}
		
		private static double lEdgeLift(Point l1, Point l2, double height){
			return ratioToScore(((l2.y - l1.y) / height) + 1);
		}
		

		private static double groupWidthLift(Point tl1, Point br1, Point tl2, Point br2) {
			final double CALCULATED_RATIO = 2 / 12.0;////NEED A CHANGE !!!
			return ratioToScore((br1.x - tl1.x)/((br2.x - tl1.x) * CALCULATED_RATIO));
		}


		public static double dTopLift(Point t1,Point t2,Point br2) {
			final double CALCULATED_RATIO =(10)/12.0;//in inches
			return ratioToScore( (t2.x-t1.x)/ ((br2.x - t1.x)*CALCULATED_RATIO));
		}
			
		private static double heightRatioLift(double h1,double h2) {
			return ratioToScore(h1/h2);
		}
	}
	
	
	
	//
	//End of the score Hendler
	//
	
	private static Scores scoreHandler(Mat threshold, List<MatOfPoint> contours, boolean target) {
		contours.sort(comparatorLargest);
		ScoreHandler scoreHandler = new ScoreHandler();
		return scoreHandler.getBestScore(threshold, contours, target);
	}
	
	//calculates the VeticalAngle the robot should turn
	private static double measureVerticalAngle(double distance,double currRatio,
			double vistionTapeHeightToCamera,double pixelsOffset,double halfRobotCm)
	{
		//System.out.println("distance -> " + distance+" ratio - "+ currRatio+ "vistioTapeHeight -> "+vistionTapeHeightToCamera+ " offset -> "+ pixelsOffset+ "half robot -> "+halfRobotCm);
		double veticalDistance = pitagoras(distance, vistionTapeHeightToCamera);
		double cmOffset = Math.abs(pixelsOffset/currRatio);
		double distanceFromOffset = pitagoras(veticalDistance,cmOffset);
		System.out.println("vertical Distance " + veticalDistance+ "cmOffset - > " + cmOffset+ "\n offset -> "+pixelsOffset);		
		double offsetDistanceFromHalfRobot  = distanceFromOffset+halfRobotCm;
		return Math.toDegrees(Math.atan(cmOffset/offsetDistanceFromHalfRobot));
	}
	
	private static double sinLaw(double a, double alpha, double b)
	{
		return Math.toDegrees(Math.asin((Math.sin(alpha)*b)/a));
	}
	private static double cosinLaw(double a, double b, double angle)
	{
		return a*a+b*b-2*a*b*Math.cos(angle);
	}
	
	private static double pitagoras(double yeter, double nitzav)
	{
		return Math.sqrt((yeter*yeter)-(nitzav*nitzav));
	}
	///
	/// the next functions are for the lift bro
	///

	
	
	//DISTANCE IN CM
	private static double measureDistance(Point t1,Point b2, double angleOfView, double heightTarget){
		double width = b2.x - t1.x;//double height = b2.y - t1.y;
		System.out.println("->>>>>>>>>>>>>> H: "+width+" :: "+feed.width()+" :::: "+20);
		return (20*feed.width()/(2*width*Math.tan(Math.toRadians(19.09089))));
	}
	//------------------------------------------------------------------
	//---------------------Contour Drawing------------------------------
	//------------------------------------------------------------------
	
	private static void drawContours(List<MatOfPoint> contours, Scalar color) {
		for(int i = 0; i < contours.size(); i++)
			Imgproc.drawContours(feed, contours, i, color);
	}
	private static void drawContours(List<MatOfPoint> contours, int r, int g, int b) {
		drawContours(contours, new Scalar(r, g, b));
	}
	private static void drawContours(List<MatOfPoint> contours) {
		drawContours(contours, new Scalar(51, 51, 51));
	}
	private static void drawContours(Scalar color, MatOfPoint...contours) {
		drawContours(Arrays.asList(contours), color);
	}
	private static void drawContours(int r, int g, int b, MatOfPoint...contours) {
		drawContours(new Scalar(r, g, b), contours);
	}
	private static void drawContours(MatOfPoint...contours) {
		drawContours(new Scalar(51, 51, 51), contours);
	}
	private static void drawRect(Rect r, Scalar color){
		Imgproc.rectangle(feed, r.tl(), r.br(), color);
	}
	
	//------------------------------------------------------------------
	//---------------------------Util-----------------------------------
	//------------------------------------------------------------------
	
	private static List<List<Point>> contoursToPointList(List<MatOfPoint> contours) {
		List<List<Point>> returnStruct = new ArrayList<List<Point>>();
		for(int i = 0; i < contours.size(); i++){
			returnStruct.add(contours.get(i).toList());
		}
		return returnStruct;
	}
	public static double avg(double...ds) {
		double all = 0;
		for(double d : ds)
			all += d;
		return all / ds.length;
	}
}
