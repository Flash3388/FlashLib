package examples.vision.filters;
import edu.flash3388.flashlib.cams.CvCamera;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.CvRunner;
import edu.flash3388.flashlib.vision.VisionProcessing;

/*
 * Example for running vision
 */
public class ExampleVision {

	public static void main(String[] args){
		
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
		 * Creates a vision runner for openCV.
		 */
		CvRunner visionRunner = new CvRunner();
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
		 */
		CvCamera camera = new CvCamera(0, 640, 480, 50);
		/*
		 * Sets the camera FPS to 30 fps
		 */
		camera.setFPS(30);
		
		/*
		 * Starts the vision runner thread
		 */
		visionRunner.start();
		
		/*
		 * Gets the delay time between its frame.
		 */
		long delay = 1 / camera.getFPS();
		/*
		 * Forever read a frame from the camera and add it to the vision runner, wait the delay time and
		 * get the analysis result to print it.
		 */
		while(true){
			visionRunner.newImage(camera.read(), (byte) 0);
			
			FlashUtil.delay(delay);
			
			Analysis an = visionRunner.getAnalysis();
			if(an != null)
				an.print();
		}
	}
}
