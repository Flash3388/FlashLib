package edu.flash3388.flashlib.cams;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.flash3388.flashlib.util.FlashUtil;

public class CvMultiCamera extends CameraView{

	private VideoCapture capture;
	private int camIndex = -1;
	private int[] cams;
	private Mat image;
	private MatOfByte buffer;
	private MatOfInt compressParams;
	private int width, height;
	
	public CvMultiCamera(String name, int current, int width, int height, int quality) {
		super(name, null);
		capture = new VideoCapture();
		cams = checkCameras(capture, 10);
		if(current >= 0){
			capture.open(current);
			camIndex = current;
		}
		image = new Mat();
		buffer = new MatOfByte();
		compressParams = new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality);
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
		this.height = height;
		this.width = width;
		
		for (int i = 0; i < cams.length; i++) {
			if(cams[i] >= 0){
				add(null);
			}
		}
	}
	public CvMultiCamera(int current, int width, int height, int quality) {
		this("", current, width, height, quality);
	}

	@Override
	public Camera currentCamera() {
		int index = getSelector() != null? getSelector().getCameraIndex() : 0;
		if(index < 0 || index >= cams.length){
			FlashUtil.getLog().reportError("Camera selector index is out of bounds "+index);
			return null;
		}
		if(camIndex != index){
			camIndex = cams[index];
			FlashUtil.getLog().log("New index "+index+":"+camIndex);
			if(camIndex >= 0)
				open(camIndex);
		}
		return this;
	}
	public void open(int dev) {
		if(capture.isOpened())
			capture.release();
		capture.open(dev);
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
	}
	@Override
	public int getQuality() {
		return 0;
	}
	@Override
	public int getFPS() {
		return (int) capture.get(Videoio.CAP_PROP_FPS);
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
		compressParams.put(0, 0, new int[]{Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality});
	}
	@Override
	public byte[] getData() {
		if(read() == null) return null;
		Imgcodecs.imencode(".jpg", image, buffer, compressParams);
		byte[] imageArr = new byte[(int) (buffer.total() * buffer.elemSize())];
		buffer.get(0, 0, imageArr);
		return imageArr;
	}
	public Mat read(){
		currentCamera();
		if(!capture.isOpened()) return null;
		capture.read(image); 
		if(image.empty()) {
			FlashUtil.getLog().log("CvMultiCamera image empty "+camIndex);
			return null;
		}
		return image;
	}
	
	public static int[] checkCameras(VideoCapture cap, int max){
		int[] cams = new int[max];
		boolean end = false;
		for (int i = 0; i < max; i++) {
			if(!end){
				if(!cap.open(i)){
					cams[i] = -1;
					end = true;
				}else cams[i] = i;
				cap.release();
			}else cams[i] = -1;
		}
		return cams;
	}
}
