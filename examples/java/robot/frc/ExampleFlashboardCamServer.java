package examples.robot.frc;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.ManualViewSelector;
import edu.flash3388.flashlib.cams.cv.CvCamera;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.frc.IterativeFRCRobot;

/*
 * A simple example showcasing the use of flashboard's camera server
 */
public class ExampleFlashboardCamServer extends IterativeFRCRobot {
	
	Camera camera;
	ManualViewSelector camSelector;
	
	@Override
	protected void initRobot() {
		/*
		 * Create a new camera using an openCV implementation. The camera is at device index 0,
		 * we use 320x240 resolution with compression quality of 50% for data sending.
		 */
		camera = new CvCamera(0, 320, 240, 50);
		
		/*
		 * Add the camera to the flashboard's camera view. The camera view can show one camera
		 * at a time, but it is possible to switch between camera feed in the camera view. Note that
		 * if the camera server was not initialized, this will cause a nullpointerexception.
		 */
		Flashboard.getCameraView().add(camera);
		
		/*
		 * If we have several cameras, we can use a camera view selector wo choose which to show.
		 * We create a manual implementation and set it to the camera view.
		 * To select a camera we can call camSelector.select(index);
		 */
		camSelector = new ManualViewSelector();
		Flashboard.getCameraView().setSelector(camSelector);
		
		/*
		 * Starts the flashboard
		 */
		Flashboard.start();
	}
	
	@Override
	protected void teleopInit() {
	}
	@Override
	protected void teleopPeriodic() {
	}

	@Override
	protected void autonomousInit() {}
	@Override
	protected void autonomousPeriodic() {}

	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}
}
