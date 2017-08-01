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

public class CvTemplateMatcher {
	
	public static enum Method {SQDIFF,SQDIFF_NORMED,TM_CCORR,TM_CCORR_NORMED,TM_COEFF,TM_COEFF_NORMED};
	
	private Method method;
	private Mat template;
	
	public CvTemplateMatcher(Mat templ,Method m) {
		method = m;
		this.template= templ;
	}
	public MatchResult match(Mat scene,int resizeFactor){
		return match(scene,template,method,resizeFactor);
	}
	
	public static MatchResult match(Mat scene, Mat[] templs, Method method, int scaleFactor){
			
		MatchRunner[] t = new MatchRunner[templs.length];
		ExecutorService sr = Executors.newFixedThreadPool(templs.length);
		for(int i = 0; i < templs.length; i++)
		{
			t[i] = new MatchRunner(scene,templs[i],method.TM_CCORR,scaleFactor);
			sr.execute(t[i]);
			
		}
		try {
			sr.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sr.shutdownNow();
		
		 MatchResult best = null;
		for(MatchRunner match : t){
			if(best == null || best.maxVal < match.result.maxVal )
			{
				best = match.result;
			}
			if(best != null){
				
				Imgproc.circle(scene, new Point(best.center.x +best.scaleFactor,best.center.y +best.scaleFactor) ,3,new Scalar(50,203,122));
				Imgcodecs.imwrite("test.png", scene);
			}
		}
	}
	
	
	
	public static MatchResult match(Mat scene, Mat templ, Method method, int scaleFactor){
		return match(scene, new Mat[]{templ}, method, scaleFactor);
	}
	
	public static class MatchResult{
		public Point center;
		public int scaleFactor;
		public double maxVal;
		
		public MatchResult(Point center,int scaleFactor,double maxVal) {
			this.center = center;
			this.scaleFactor = scaleFactor;
			this.maxVal = maxVal;
		}
	}
	
	private static class MatchRunner implements Runnable
	{
		private Mat scene;
		private Mat templ;
		private Method method;
		private int scaleFactor;
		public MatchResult result;
		
		public MatchRunner(Mat scene, Mat templ, Method method, int scaleFactor)
		{
			this.scene = scene;
			this.templ = templ;
			this.method = method;
			this.scaleFactor = scaleFactor;
		}

		@Override
		public void run() {
			result = match(scene, templ, method, scaleFactor);
			
		}
		public MatchResult match(Mat scene, Mat templ, Method method, int scaleFactor){
			int tw = templ.width();
			int th = templ.height();
			int currScaleFactor = scaleFactor;
			MatchResult bestScore = null;
			
			for(Mat img = scene.clone(); img.width() > tw && img.height() > th;
					CvProcessing.resize(img, scaleFactor)){
				
				MatchResult currResult = match(scene, templ, method, img);
		        
		        if(bestScore == null || bestScore.maxVal < currResult.maxVal){
		        	bestScore = currResult;
		        	bestScore.scaleFactor = currScaleFactor;
		        }
		        currScaleFactor  += scaleFactor;
		     }
			
			
	       	return bestScore;
		}

		public MatchResult match(Mat scene, Mat templ, Method method, Mat img) {
			
			int result_cols = img.cols() - templ.cols() + 1;
			int result_rows = img.rows() - templ.rows() + 1;
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
			
			MatchResult currResult = new MatchResult(new Point(matchLoc.x +(templ.cols()/2),matchLoc.y +(templ.rows()/2)),0,maxVal);
			return currResult;
		}
		
		
	}

}
