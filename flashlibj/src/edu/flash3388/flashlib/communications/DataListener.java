package edu.flash3388.flashlib.communications;

/**
 * Listener for image data read by a {@link CameraClient}. Can be added by calling {@link CameraClient#addListener(DataListener)}.
 * Once added, image data that is read be the camera client is passed to the implemented method {@link #newData(byte[])}
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface DataListener {
	/**
	 * Receives image data which was read by a {@link CameraClient} object for image usage.
	 * @param data image data array
	 */
	void newData(byte[] data);
}
