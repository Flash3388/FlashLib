package edu.flash3388.flashlib.cams;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.flash3388.flashlib.util.FlashUtil;


public class CvCamera implements Camera{

	private int camIndex;
	private Mat image;
	private MatOfByte buffer;
	private MatOfInt compressParams;
	private VideoCapture capture;
	
	private int quality;
	
	public CvCamera(int cam, int width, int height, int quality) throws Exception{
		capture = new VideoCapture();
		capture.open(cam);
		if(!capture.isOpened())
			throw new Exception("Unable to open camera " + cam);
		
		image = new Mat();
		buffer = new MatOfByte();
		compressParams = new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality);
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
		
		camIndex = cam;
		this.quality = quality;
	}
	public CvCamera(int cam, int width, int height) throws Exception{
		this(cam, width, height, DEFAULT_QUALITY);
	}
	public CvCamera(int cam) throws Exception{
		this(cam, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_QUALITY);
	}
	
	@Override
	public byte[] getData() {
		if(!capture.isOpened()) return null;
		if(read() == null) return null;
		Imgcodecs.imencode(".jpg", image, buffer, compressParams);
		byte[] imageArr = new byte[(int) (buffer.total() * buffer.elemSize())];
		buffer.get(0, 0, imageArr);
		return imageArr;
	}
	public Mat read(){
		if(!capture.isOpened()) return null;
		capture.read(image);
		if(image.empty()) {
			FlashUtil.getLog().log("CvCamera image empty "+camIndex);
			return null;
		}
		return image;
	}
	
	public double getCaptureProperty(int propId){
		return capture.get(propId);
	}
	@Override
	public int getQuality() {
		return quality;
	}
	@Override
	public int getFPS() {
		return (int) capture.get(Videoio.CAP_PROP_FPS);
	}
	
	public void setCaptureProperty(int propId, double value){
		capture.set(propId, value);
	}
	@Override
	public void setFPS(int fps) {
		if(fps < 10 || fps > 60)
			throw new IllegalArgumentException("FPS value is not value! [10..60]");
		capture.set(Videoio.CAP_PROP_FPS, fps);
	}
	@Override
	public void setQuality(int quality) {
		if(quality < 1 || quality > 100)
			throw new IllegalArgumentException("Quality value is not value! [1..100]");
		this.quality = quality;
		compressParams.put(0, 0, new int[]{Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality});
	}
	
	public int height(){
		return (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
	}
	public int width(){
		return (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
	}
	public void setSize(int width, int height){
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
	}
	
	public void release(){
		if(!capture.isOpened())
			throw new IllegalStateException("Camera is already closed and cannot be released");
		capture.release();
	}
}
