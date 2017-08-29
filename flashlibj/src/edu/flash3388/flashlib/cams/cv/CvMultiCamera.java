package edu.flash3388.flashlib.cams.cv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.CameraView;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Implements a camera interface using openCV. Opens a {@link VideoCapture}
 * and allows for switching of device indexes. Extends {@link CameraView}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class CvMultiCamera extends CameraView{

	private VideoCapture capture;
	private int camIndex = -1;
	private int[] cams;
	private Mat image;
	private MatOfByte buffer;
	private MatOfInt compressParams;
	
	private int quality, width, height;
	
	/**
	 * Opens a new camera using openCV with a given frame width and height, 
	 * and a compression quality.
	 * <p>
	 * Checks all available devices up to index 10 and adds them using
	 * {@link CameraView#add(Camera)}.
	 * </p>
	 * 
	 * @param name the name of the camera
	 * @param current the device index from 0.
	 * @param width the frame width
	 * @param height the frame height
	 * @param quality the compression quality
	 * 
	 * @throws RuntimeException if the camera could not be opened
	 */
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
		this.quality = quality;
		
		for (int i = 0; i < cams.length; i++) {
			if(cams[i] >= 0){
				add(null);
			}
		}
	}
	/**
	 * Opens a new camera using openCV with a given frame width and height, 
	 * and a compression quality.
	 * <p>
	 * Checks all available devices up to index 10 and adds them using
	 * {@link CameraView#add(Camera)}.
	 * </p>
	 * 
	 * @param current the device index from 0.
	 * @param width the frame width
	 * @param height the frame height
	 * @param quality the compression quality
	 * 
	 * @throws RuntimeException if the camera could not be opened
	 */
	public CvMultiCamera(int current, int width, int height, int quality) {
		this("", current, width, height, quality);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks to see if the new camera is available from the checked cameras.
	 * If it does, the index is set, by closing the video capture and reopening it
	 * at the new index.
	 * </p>
	 */
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
	/**
	 * Opens the {@link VideoCapture} at the new index with all the previous
	 * camera settings.
	 * Closes the capture if it is opened.
	 * 
	 * @param dev the new device index
	 */
	public void open(int dev) {
		if(capture.isOpened())
			capture.release();
		capture.open(dev);
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getQuality() {
		return quality;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the video capture property of {@link Videoio#CAP_PROP_FPS}
	 * </p>
	 * @see VideoCapture#get(int)
	 */
	@Override
	public int getFPS() {
		return (int) capture.get(Videoio.CAP_PROP_FPS);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the video capture property of {@link Videoio#CAP_PROP_FPS}
	 * </p>
	 * @see VideoCapture#set(int, double)
	 * @throws IllegalArgumentException if fps is smaller than 10 or larger than 60
	 */
	@Override
	public void setFPS(int fps) {
		if(fps < 10 || fps > 60)
			throw new IllegalArgumentException("FPS value is not value! [10..60]");
		capture.set(Videoio.CAP_PROP_FPS, fps);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the video compression matrix with a new{@link Imgcodecs#CV_IMWRITE_JPEG_QUALITY}
	 * value
	 * </p>
	 * @throws IllegalArgumentException if quality is smaller than 1 or larger than 100
	 */
	@Override
	public void setQuality(int quality) {
		if(quality < 1 || quality > 100)
			throw new IllegalArgumentException("Quality value is not value! [1..100]");
		compressParams.put(0, 0, new int[]{Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality});
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Compresses the frame into a .jpg.
	 * </p>
	 */
	@Override
	public byte[] getData() {
		if(read() == null) return null;
		Imgcodecs.imencode(".jpg", image, buffer, compressParams);
		byte[] imageArr = new byte[(int) (buffer.total() * buffer.elemSize())];
		buffer.get(0, 0, imageArr);
		return imageArr;
	}
	/**
	 * Reads a frame from the camera and returns a {@link Mat} object.
	 * @return a new frame
	 * @see VideoCapture#read(Mat)
	 */
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
	
	private static int[] checkCameras(VideoCapture cap, int max){
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
