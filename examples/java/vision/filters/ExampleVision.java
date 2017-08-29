package examples.vision.filters;

import org.opencv.core.Core;

import edu.flash3388.flashlib.cams.cv.CvCamera;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.SimpleVisionRunner;
import edu.flash3388.flashlib.vision.VisionProcessing;
import edu.flash3388.flashlib.vision.VisionRunner;
import edu.flash3388.flashlib.vision.cv.CvSource;

/*
 * Example for running vision
 */
public class ExampleVision {

	public static void main(String[] args){
		/*
		 * Loads the openCV native library 
		 */
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		/*
		 * Loads a processing object from a file.
		 */
		VisionProcessing processing = VisionProcessing.createFromXml("filters.xml");
		
		/*
		 * If the file doesn't exist, or an error occurred while parsing XML, we exit.
		 */
		if(processing == null){
			System.out.println("Unable to load the processing object");
			return;
		}
		
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
		 * Opens a new camera using openCV. The device index is 0, the frame width 640,
		 * the frame height 480, the compression quality is 50%.
		 * 
		 * It is not mendetory to use this implementation of a camera to work with the vision runner.
		 * Any camera interfacing is possible, just the resulting image must be an OpenCV Mat object.
		 */
		CvCamera camera = new CvCamera(0, 640, 480, 50);
		/*
		 * Sets the camera FPS to 30 fps
		 */
		camera.setFPS(30);
		
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
