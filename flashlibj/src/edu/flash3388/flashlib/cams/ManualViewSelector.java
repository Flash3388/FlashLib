package edu.flash3388.flashlib.cams;

/**
 * Provides a manual selector for {@link CameraView}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ManualViewSelector implements CameraViewSelector{

	private int cameraIndex = 0;
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if index is negative
	 */
	@Override
	public void select(int index){
		if(index < 0)
			throw new IllegalArgumentException("Camera index cannot be negative");
		cameraIndex = index;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCameraIndex() {
		return cameraIndex;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void newCam(Camera cam) {}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remCam(Camera cam) {}
}
