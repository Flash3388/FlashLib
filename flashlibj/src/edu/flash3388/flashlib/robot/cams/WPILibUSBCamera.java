package edu.flash3388.flashlib.robot.cams;

import org.opencv.core.Mat;

import edu.flash3388.flashlib.cams.Camera;
import edu.wpi.cscore.CvSink;

public class WPILibUSBCamera implements Camera{

	private CvSink sink;
	private Mat image;
	
	public WPILibUSBCamera(String name){
		sink = new CvSink(name);
		image = new Mat();
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
	public void setFPS(int fps) {}
	@Override
	public void setQuality(int quality) {}
	@Override
	public byte[] getData() {
		retreive();
		byte[] imageArr = new byte[(int) (image.total() * image.elemSize())];
		image.get(0, 0, imageArr);
		return imageArr;
	}
	public Mat retreive(){
		sink.grabFrame(image);
		return image;
	}
}
