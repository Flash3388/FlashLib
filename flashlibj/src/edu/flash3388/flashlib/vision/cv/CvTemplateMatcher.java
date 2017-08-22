package edu.flash3388.flashlib.vision.cv;

import static org.opencv.core.CvType.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import edu.flash3388.flashlib.vision.MatchResult;
import edu.flash3388.flashlib.vision.TemplateMatcher;

/**
 * Provides template matching algorithm using openCV
 * 
 * @author Alon Klein
 * @since FlashLib 1.0.1
 */
public class CvTemplateMatcher implements TemplateMatcher{
	
	public static enum Method {SQDIFF,SQDIFF_NORMED,TM_CCORR,TM_CCORR_NORMED,TM_COEFF,TM_COEFF_NORMED};
	
	private Method method;
	private Mat templates[];
	
	public CvTemplateMatcher(Mat templ, Method m) {
		this(new Mat[]{templ}, m);
	}
	public CvTemplateMatcher(Mat[] templ, Method m) {
		method = m;
		this.templates = templ;
	}
	
	public MatchResult match(Mat scene, double scaleFactor){
		return match(scene, templates, method, scaleFactor);
	}
	
	public static MatchResult match(Mat scene, Mat[] templs, Method method, double scaleFactor){
			
		MatchRunner[] t = new MatchRunner[templs.length];
		ExecutorService sr = Executors.newFixedThreadPool(templs.length);
		for(int i = 0; i < templs.length; i++){
			t[i] = new MatchRunner(scene,templs[i],method,scaleFactor);
			sr.execute(t[i]);
		}
		try {
			sr.shutdown();
			sr.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		MatchResult best = findBestMatch(t);
		if(best != null){
			
			Imgproc.circle(scene, new Point(best.centerx +best.scaleFactor,best.centery +best.scaleFactor) ,3,new Scalar(51,51,51));
			//Imgproc.circle(scene, new Point(best.center.x ,best.center.y ) ,3,new Scalar(50,203,122));
			Imgcodecs.imwrite("/home/klein/dev/frc/test.png", scene);
			
		}
		return best;
	}
	
	private static MatchResult findBestMatch(MatchRunner[] matches) {
		MatchResult best = null;
		for(MatchRunner match : matches){
			if(match.result == null)
				continue;
			if(best == null || best.maxVal < match.result.maxVal )
				best = match.result;
		}
		return best;
	}
	
	private static class MatchRunner implements Runnable{
		private Mat scene;
		private Mat templ;
		private Method method;
		private double scaleFactor;
		public MatchResult result;
		
		public MatchRunner(Mat scene, Mat templ, Method method, double scaleFactor){
			this.scene = scene;
			this.templ = templ;
			this.method = method;
			this.scaleFactor = scaleFactor;
		}

		@Override
		public void run() {
			result = match(scene, templ, method, scaleFactor);
		}
		public MatchResult scaleTemplateMatch(Mat scene, Mat templ, Method method, double scaleFactor){
			int tw = templ.width();
			int th = templ.height();
			double currScaleFactor = scaleFactor;
			MatchResult bestScore = null;
			
			for(Mat img = templ.clone(); img.width() > tw*0.25;
					CvProcessing.resize(img, scaleFactor)){
				
				MatchResult currResult = match(scene, img, method, img);
		        
		        if(bestScore == null || bestScore.maxVal < currResult.maxVal){
		        	bestScore = currResult;
		        	bestScore.scaleFactor = currScaleFactor;
		        }
		        currScaleFactor  *= scaleFactor;
		     }
			
			
	       	return bestScore;
		}

		public MatchResult match(Mat scene, Mat templ, Method method, double scaleFactor){
			int tw = templ.width();
			int th = templ.height();
			double currScaleFactor = scaleFactor;
			MatchResult bestScore = null;
			
			for(Mat img = scene.clone(); img.width() > tw && img.height() > th;
					CvProcessing.resize(img, scaleFactor)){
				
				MatchResult currResult = match(img, templ, method, img);
		        
		        if(bestScore == null || bestScore.maxVal < currResult.maxVal){
		        	bestScore = currResult;
		        	bestScore.scaleFactor = currScaleFactor;
		        }
		        currScaleFactor  *= scaleFactor;
		     }
			
			
	       	return bestScore;
		}

		public MatchResult match(Mat scene, Mat templ, Method method, Mat img) {
			
			int result_cols = scene.cols() - templ.cols() + 1;
			int result_rows = scene.rows() - templ.rows() + 1;
			Mat result = new Mat(result_rows, result_cols, CV_32FC1);
			Imgproc.matchTemplate(scene, templ, result, method.ordinal());
			//Core.normalize(result, result, 0, 1, 32,-1,new Mat());
				
			MinMaxLocResult mmr = Core.minMaxLoc(result);

			
			Point matchLoc;
			double maxVal;
			if (method.ordinal() == Imgproc.TM_SQDIFF
			        || method.ordinal() == Imgproc.TM_SQDIFF_NORMED) {
			    
				matchLoc = mmr.minLoc;
				maxVal = mmr.minVal;
			}
			else {
			    matchLoc = mmr.maxLoc;
			    maxVal = mmr.maxVal;
			}
			
			MatchResult currResult = new MatchResult(matchLoc.x +(templ.cols()/2),matchLoc.y +(templ.rows()/2),0,maxVal);
			return currResult;
		}
		
		
	}
}
