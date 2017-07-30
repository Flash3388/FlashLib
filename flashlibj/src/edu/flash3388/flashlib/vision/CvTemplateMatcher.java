package edu.flash3388.flashlib.vision;

import static org.opencv.core.CvType.*;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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
	public TemplateResult match(Mat scene,int resizeFactor)
	{
		return match(scene,template,method,resizeFactor);
	}
	
	public static TemplateResult match(Mat scene,Mat templ,Method method, int scaleFactor)
	{
		
		int tw = templ.width();
		int th = templ.height();
		int currScaleFactor = scaleFactor;
		TemplateResult bestScore = null;
		
		for(Mat img = scene.clone(); img.width() > tw && img.height() > th;
				CvProcessing.resize(img, scaleFactor))
		{
			
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
	        else
	        {
	            matchLoc = mmr.maxLoc;
	            maxVal = mmr.maxVal;
	        }
	        
	        if(bestScore == null || bestScore.maxVal > mmr.maxVal)
	       	{
	        	Point center = new Point(matchLoc.x +(templ.cols()/2),matchLoc.y +(templ.rows()/2));    
	        	bestScore = new TemplateResult(center,currScaleFactor,maxVal);
	        }
	        currScaleFactor  += scaleFactor;
	     }
		if(bestScore != null)
		{
			Imgproc.rectangle(scene, new Point(bestScore.center.x - tw *0.5,bestScore.center.y - th *0.5), new Point(bestScore.center.x + tw *0.5,bestScore.center.y + th *0.5), new Scalar(203,20,150));
			Imgproc.circle(scene, new Point(bestScore.center.x +bestScore.scaleFactor,bestScore.center.y +bestScore.scaleFactor) ,3,new Scalar(50,203,122));
			Imgcodecs.imwrite("test.png", scene);
		}
		
       	return bestScore;
	}
	
	public static class TemplateResult
	{
		public Point center;
		public int scaleFactor;
		public double maxVal;
		
		public TemplateResult(Point center,int scaleFactor,double maxVal) {
			this.center = center;
			this.scaleFactor = scaleFactor;
			this.maxVal = maxVal;
		}
	}

}
