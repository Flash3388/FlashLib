package edu.flash3388.flashlib.cams;

/**
 * Provides an index selector for cameras in a {@link CameraView}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface CameraViewSelector {
	/**
	 * Reports a new camera added to a {@link CameraView}.
	 * @param cam a new camera
	 */
	void newCam(Camera cam);
	/**
	 * Reports a camera to remove from a {@link CameraView}.
	 * @param cam a camera to remove 
	 */
	void remCam(Camera cam);
	/**
	 * Selects a camera index to use by a {@link CameraView}.
	 * @param index index of filter to select
	 */
	void select(int index);
	/**
	 * Gets the selected index of camera
	 * @return the selected camera
	 */
	int getCameraIndex();
}
