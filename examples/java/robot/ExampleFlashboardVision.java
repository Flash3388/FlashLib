package examples.robot;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.cv.CvCamera;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.GrayFilter;
import edu.flash3388.flashlib.vision.LargestFilter;
import edu.flash3388.flashlib.vision.Vision;
import edu.flash3388.flashlib.vision.VisionProcessing;

/*
 * In this example we will review usage of Flashboard integrated vision with robots. We will
 * use IterativeRobot here, but the principles remain the same for all robot
 * bases.
 * 
 * Please make sure you have checked ExampleFlashboard first to learn about Flashboard control 
 * initialization and ExampleFlashboardCameraServer to learn about flashboard and camera data.
 */
public class ExampleFlashboardVision extends IterativeRobot{

	//our camera
	Camera camera;
	//will hold our vision control object
	Vision vision;
	
	@Override
	protected void robotInit() {
		/*
		 * Flashboard is integrated with FlashLib's vision system to allow users to perform
		 * image processing with it and send the data to the robot. The vision is performed on
		 * images received from the camera server and the results are sent through the standard
		 * communications. So it is necessary to make sure both are provided, not necessarily from 
		 * the same robot, but in total.
		 * 
		 * Here we will provide both the camera data and standard communications and not separate them.
		 */
		
		/*
		 * Let's start by creating a camera. Here we will use one camera, but since the camera server can hold
		 * several and always sends data about one, it will always work the same.
		 */
		camera = new CvCamera(0, 320, 240, 50);
		Flashboard.getCameraView().add(camera);
		
		/*
		 * Flashboard controls provides an implementation for the Vision interface which allows
		 * control over the vision process executed by the software.
		 * We can use that object to control vision execution and add vision processing parameters.
		 */
		vision = Flashboard.getVision();
		
		/*
		 * The flashboard can receive parameters for vision processing from remote control as well as
		 * local files. So we can create a vision processing object local and attach it to the vision 
		 * control. 
		 * 
		 * Please note that it is not possible to create custom vision filters or analysis creators
		 * and send them to the flashboard since flashboard doesn't recognize them. Only built-in
		 * filters can be used.
		 * 
		 * Lets create one, add it some filters and attach it to the remote control.
		 */
		VisionProcessing processing = new VisionProcessing("proc-test");
		processing.addFilters(new GrayFilter(0, 100), new LargestFilter(10));
		
		vision.addProcessing(processing);
		
		/*
		 * It is also possible to select the index of the processing object to be used for vision
		 */
		vision.selectProcessing(0);
	}
	
	@Override
	protected void disabledInit() {
		/*
		 * When in disabled mode, we stop the vision process
		 */
		vision.stop();
	}
	@Override
	protected void disabledPeriodic() {
	}
	
	@Override
	protected void modeInit(int mode) {
		/*
		 * When in other operation modes, we start the vision process
		 */
		vision.start();
	}
	@Override
	protected void modePeriodic(int mode) {
		/*
		 * Now that the vision is running, we can periodically check if a new vision analysis data was 
		 * received by the vision object and then use it. 
		 * Here we check if the vision object holds a `new` analysis object. An analysis object
		 * is considered new if the time since it was received did not pass a pre-defined timeout.
		 * The default timeout is usually 1 second, but it can be changed by calling
		 * vision.setNewAnalysisTimeout(milliseconds);
		 */
		if(vision.hasNewAnalysis()){
			/*
			 * Retrieving the vision object. Note that the analysis object could still be
			 * considered new in the next iteration of modePeriodic. This allows us to access it
			 * from multiple places and use the same analysis.
			 */
			Analysis an = vision.getAnalysis();
			
			//lets just print it or something for this example...
			System.out.println(an.toString());
			
			/*
			 * If you are certain no other part of the robot software needs access to this
			 * analysis object and want to make sure it is not used twice because it is still 
			 * considered new, it is possible to instruct the vision to consider it as old.
			 */
			vision.setNewAnalysisAsOld();
		}
	}
}
