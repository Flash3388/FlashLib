package edu.flash3388.flashlib.cams.ni;

import java.nio.ByteBuffer;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.flash3388.flashlib.cams.Camera;

/**
 * Implements a camera interface using NIVision.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class NICamera implements Camera{

	private edu.wpi.first.wpilibj.vision.USBCamera camera;
	private Image image;
	
	/**
	 * Opens a new camera using NIVision at a certain device index with 
	 * a given frame width and height.
	 * 
	 * @param name the camera name
	 * @param width the frame width
	 * @param height the frame height
	 */
	public NICamera(String name, int width, int height){
		camera = new edu.wpi.first.wpilibj.vision.USBCamera(name);
		camera.openCamera();
		setSize(width, height);
		image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		camera.startCapture();
	}
	/**
	 * Opens a new camera using NIVision at a certain device index with 
	 * the {@link Camera#DEFAULT_WIDTH default frame width} and 
	 * the {@link Camera#DEFAULT_HEIGHT default frame height}.
	 * 
	 * @param name the camera name
	 */
	public NICamera(String name){
		this(name, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	/**
	 * Reads a frame from the camera and returns a {@link Image} object.
	 * @return a new frame
	 */
	@Override
	public Image read(){
		camera.getImage(image);
		return image;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Compresses the frame into a .jpg.
	 * </p>
	 * @see edu.wpi.first.wpilibj.vision.USBCamera#getImageData(ByteBuffer)
	 */
	@Override
	public byte[] getData() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(1);
		camera.getImageData(buffer);
		int start = 0;
		while (start < buffer.limit() - 1) {
	        if ((buffer.get(start) & 0xff) == 0xFF && (buffer.get(start + 1) & 0xff) == 0xD8)
	          break;
	        start++;
	    }

	    if (buffer.limit() - start - 1 <= 2) 
	    	return null;
	    
	    buffer.position(start);
        byte[] imageArray = new byte[buffer.remaining()];
        buffer.get(imageArray, 0, buffer.remaining());
        return imageArray;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getQuality() {
		return 30;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFPS() {
		return 30;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFPS(int fps) {
		camera.setFPS(fps);
	}
	@Override
	public void setQuality(int quality) {}
	/**
	 * Sets the frame width and height.
	 * @param width new width
	 * @param height new height
	 */
	public void setSize(int width, int height){
		camera.setSize(width, height);
		camera.updateSettings();
	}
}
