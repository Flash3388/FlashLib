package frcTesting;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgcodecs.Imgcodecs;

import edu.flash3388.flashlib.cams.CvCamera;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.AnalysisCreator;
import edu.flash3388.flashlib.vision.ColorFilter;
import edu.flash3388.flashlib.vision.TemplateAnalysisCreator;
import edu.flash3388.flashlib.vision.VisionProcessing;
import edu.flash3388.flashlib.vision.cv.CvSource;
import edu.flash3388.flashlib.vision.cv.CvTemplateMatcher;

public class ExampleTemplateMatching {

	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		/*
		 * HSV ranges for the color filter
		 */
		int min_hue = 0,
			max_hue = 180,
			min_saturation = 0,
			max_saturation = 255,
			min_value = 230,
			max_value = 255;
		
		
		/*
		 * Creates a processing object
		 */
		VisionProcessing processing = new VisionProcessing("processing test");
		
		/*
		 * Creates an HSV filter
		 */
		ColorFilter hsvFilter = new ColorFilter(true, 
				min_hue, max_hue, 
				min_saturation, max_saturation, 
				min_value, max_value);
		
		processing.addFilter(hsvFilter);
		/*
		 * setting up templateAnalisysCreator
		 */
		
		String path = "~/frc/templates"; //path of the directory of the templates 
		CvTemplateMatcher.Method method = CvTemplateMatcher.Method.TM_CCORR_NORMED; //template method - for binary images TM_CCORR_NORMED is recomended
		double scaleFactor = 0.9; //scale factor to allow different scales
		double realHeight = 10; // real life height of the target
		double fieldOfView = Math.toRadians(45);
		AnalysisCreator templateAnalisysCreator = new TemplateAnalysisCreator("/home/klein/dev/frc/templates",method.ordinal(),scaleFactor
				,realHeight,0,fieldOfView,true);
		/*
		 * Adds the filters to the processing object
		 */
		processing.setAnalysisCreator(templateAnalisysCreator);
		
		/* 
		 * Opens a new camera using openCV. The device index is 0, the frame width 640,
		 * the frame height 480, the compression quality is 50%.
		 */
		CvCamera camera = new CvCamera(0, 640, 480, 50);
		
		/*
		 * Gets the delay time between its frame.
		 */
		long delay = 1 / camera.getFPS();
		
		CvSource source = new CvSource();
		
		while(true)
		{
			source.prep(Imgcodecs.imread("/home/klein/dev/frc/image.png",CvType.CV_8UC1));
			FlashUtil.delay(delay);
			Analysis an = processing.processAndGet(source);	
			if(an != null)
				System.out.println(an.toString());			
		}
	}

}
