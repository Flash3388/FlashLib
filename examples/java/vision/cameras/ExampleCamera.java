package examples.vision.cameras;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.flash3388.flashlib.cams.CvCamera;

/*
 * Example for using openCV camera.
 */
public class ExampleCamera {

	public static void main(String[] args){
		/*
		 * Loads the native openCV library
		 */
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
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
		 * Reads a frame from the camera
		 */
		Mat frame = camera.read();
		
		/*
		 * Closes the camera
		 */
		camera.release();
	}
}
