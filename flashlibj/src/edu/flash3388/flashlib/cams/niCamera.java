package edu.flash3388.flashlib.cams;

import java.nio.ByteBuffer;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.flash3388.flashlib.cams.Camera;

public class niCamera implements Camera{

	private edu.wpi.first.wpilibj.vision.USBCamera camera;
	private Image image;
	
	public niCamera(String name, int width, int height){
		camera = new edu.wpi.first.wpilibj.vision.USBCamera(name);
		camera.openCamera();
		setSize(width, height);
		image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		camera.startCapture();
	}
	public niCamera(String name){
		this(name, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	public Image read(){
		camera.getImage(image);
		return image;
	}
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
	@Override
	public int getQuality() {
		return 30;
	}
	@Override
	public int getFPS() {
		return 30;
	}
	@Override
	public void setFPS(int fps) {
		camera.setFPS(fps);
	}
	@Override
	public void setQuality(int quality) {}
	public void setSize(int width, int height){
		camera.setSize(width, height);
		camera.updateSettings();
	}
}
