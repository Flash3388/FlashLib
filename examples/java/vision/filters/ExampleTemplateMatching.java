package examples.vision.filters;

import org.opencv.core.Core;

import edu.flash3388.flashlib.cams.cv.CvCamera;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.AnalysisCreator;
import edu.flash3388.flashlib.vision.ColorFilter;
import edu.flash3388.flashlib.vision.SimpleVisionRunner;
import edu.flash3388.flashlib.vision.TemplateAnalysisCreator;
import edu.flash3388.flashlib.vision.VisionProcessing;
import edu.flash3388.flashlib.vision.VisionRunner;
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
		//adding the color filter to the processing object
		processing.addFilter(hsvFilter);
		
		
		//path to the templates to use for the template matching
		String path = "/templates"; 
		//template method - for binary images TM_CCORR_NORMED is recomended
		int method = CvTemplateMatcher.Method.TM_CCORR_NORMED.ordinal(); 
		//scale factor to allow different scales
		double scaleFactor = 0.9; 
		// real life height of the target
		double realHeight = 10; 
		//camera field of view, needs to be in radians
		double fieldOfView = Math.toRadians(45);
		
		/*
		 * creating the template analysis creator with our parameters
		 */
		AnalysisCreator templateAnalisysCreator = new TemplateAnalysisCreator(path, method, scaleFactor
				,realHeight, 0.0, fieldOfView, true, true);
		/*
		 * Setting the processing's analysis creator the our template analysis creator
		 */
		processing.setAnalysisCreator(templateAnalisysCreator);
		
		/* 
		 * Opens a new camera using openCV. The device index is 0, the frame width 640,
		 * the frame height 480, the compression quality is 50%.
		 */
		CvCamera camera = new CvCamera(0, 640, 480, 50);
		
		
		/*
		 * Creates a simple vision runner
		 */
		VisionRunner visionRunner = new SimpleVisionRunner();
		/*
		 * Set the vision source to a openCV implementation
		 */
		visionRunner.setVisionSource(new CvSource());
		/*
		 * Adds a processing object to the vision runner
		 */
		visionRunner.addProcessing(processing);
		/*
		 * Selects the processing index to use
		 */
		visionRunner.selectProcessing(0);
		/*
		 * Starts the vision runner. Since we are using a simple vision runner
		 * implementation, this call merely resets data and changes isRunning() to indicate
		 * the vision process is active
		 */
		visionRunner.start();
		
		/*
		 * Gets the time period between 2 camera frames (1 / frequency = period).
		 */
		long peiod = 1 / camera.getFPS();
		/*
		 * Forever read a frame from the camera and add it to the vision runner, attempt
		 * analysis and wait the camera frame period
		 */
		while(true){
			//set a new image to the vision runner
			visionRunner.frameProperty().setValue(camera.read());
			
			//attempt analysis of the frame. If returns true then we got a new
			//analysis so get the analysis and print
			if(visionRunner.analyze()){
				Analysis an = visionRunner.getAnalysis();
				System.out.println("new analysis: "+an.toString());
			}
			
			FlashUtil.delay(peiod);	
		}
	}

}
