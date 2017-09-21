package examples.robot;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.CameraViewSelector;
import edu.flash3388.flashlib.cams.cv.CvCamera;
import edu.flash3388.flashlib.cams.cv.CvQueueCamera;
import edu.flash3388.flashlib.flashboard.DashboardCamViewSelector;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.sbc.IterativeRobot;

/*
 * In this example we will review usage of Flashboard's camera server with robots. We will
 * use IterativeRobot here, but the principles remain the same for all robot
 * bases.
 */
public class ExampleFlashboardCameraServer extends IterativeRobot{
	
	Camera cam1;
	Camera cam2;
	CameraViewSelector camSelector;
	
	@Override
	protected void robotInit() {
		/*
		 * Lets see how to use the Flashboard camera server. Please make sure you have checked ExampleFlashboard
		 * first to learn about Flashboard control initialization.
		 * 
		 * The bellow code will throw an exception if flashboard camera server was not initialized.
		 */
		
		/*
		 * The camera server can work with multiple cameras, but will handle data from only one at any
		 * given time. So we can add multiple cameras to it, but we have to select one. 
		 * Here we will use 2 cameras and switch control between them if necessary.
		 */
		
		/*
		 * Our first camera will be an OpenCV camera implementation. Please make sure
		 * OpenCV binaries are available and that the native OpenCV library has been 
		 * loaded (System.loadLibrary(Core.NATIVE_LIBRARY_NAME);)
		 * 
		 * We set up our camera to camera index 0, resolution 320x240 and compression quality of 50%.
		 * Compression quality dictates the quality of the image when it is sent.
		 */
		cam1 = new CvCamera(0, 320, 240, 50);
		
		/*
		 * Our second camera is a CvQueueCamera, which is a simple queue of openCV images.
		 */
		cam2 = new CvQueueCamera();
		
		/*
		 * Now we can add our cameras to the flashboard camera view
		 */
		Flashboard.getCameraView().add(cam1, cam2);
		
		/*
		 * Now that we setup our cameras Let's add a way to select which camera to use.
		 * 
		 * We will create a Dashboard camView selector. This will allow us to select the view from 
		 * Flashboard. But! if standard flashboard communications were not initialized, this won't work.
		 * Instead we can implement the CameraViewSelector interface differently, or maybe use the
		 * ManualCamViewSelector.
		 */
		camSelector = new DashboardCamViewSelector();
		Flashboard.getCameraView().setSelector(camSelector);
		
		/*
		 * To select a camera we can just call select and pass the wanted camera index.
		 */
		camSelector.select(1);
		
		/*
		 * Now our cameras are ready and will be automatically used. calling Flashboard.start()
		 * is only necessary when using the standard Flashboard communications.
		 */
		
	}

	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}
	
	@Override
	protected void modeInit(int mode) {
	}
	@Override
	protected void modePeriodic(int mode) {
	}
}
