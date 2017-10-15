package edu.flash3388.flashlib.cams.cv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Implements a camera interface using openCV. Opens a {@link VideoCapture}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class CvCamera implements Camera{

	private int camIndex;
	private Mat image;
	private MatOfByte buffer;
	private MatOfInt compressParams;
	private VideoCapture capture;
	
	private int quality;
	
	/**
	 * Opens a new camera using openCV at a certain device index with 
	 * a given frame width and height, and a compression quality.
	 * 
	 * @param cam the device index from 0.
	 * @param width the frame width
	 * @param height the frame height
	 * @param quality the compression quality
	 * 
	 * @throws RuntimeException if the camera could not be opened
	 */
	public CvCamera(int cam, int width, int height, int quality){
		capture = new VideoCapture();
		capture.open(cam);
		if(!capture.isOpened())
			throw new RuntimeException("Unable to open camera " + cam);
		
		image = new Mat();
		buffer = new MatOfByte();
		compressParams = new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality);
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
		
		camIndex = cam;
		this.quality = quality;
	}
	/**
	 * Opens a new camera using openCV at a certain device index with 
	 * a given frame width and height, and the 
	 * {@link Camera#DEFAULT_QUALITY default compression quality}.
	 * 
	 * @param cam the device index from 0.
	 * @param width the frame width
	 * @param height the frame height
	 * 
	 * @throws RuntimeException if the camera could not be opened
	 */
	public CvCamera(int cam, int width, int height){
		this(cam, width, height, DEFAULT_QUALITY);
	}
	/**
	 * Opens a new camera using openCV at a certain device index with 
	 * the {@link Camera#DEFAULT_WIDTH default frame width} and 
	 * the {@link Camera#DEFAULT_HEIGHT default frame height}, 
	 * and the {@link Camera#DEFAULT_QUALITY default compression quality}.
	 * 
	 * @param cam the device index from 0.
	 * 
	 * @throws RuntimeException if the camera could not be opened
	 */
	public CvCamera(int cam){
		this(cam, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_QUALITY);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Compresses the frame into a .jpg.
	 * </p>
	 */
	@Override
	public byte[] getData() {
		if(!capture.isOpened()) return null;
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
	@Override
	public Mat read(){
		if(!capture.isOpened()) return null;
		capture.read(image);
		if(image.empty()) {
			FlashUtil.getLog().log("CvCamera image empty "+camIndex);
			return null;
		}
		return image;
	}
	
	/**
	 * Gets a property from the {@link VideoCapture}.
	 * 
	 * @param propId id of the property
	 * @return the value of the property
	 * @see VideoCapture#get(int)
	 */
	public double getCaptureProperty(int propId){
		return capture.get(propId);
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
	 * @see #getCaptureProperty(int)
	 */
	@Override
	public int getFPS() {
		return (int) capture.get(Videoio.CAP_PROP_FPS);
	}
	
	/**
	 * Sets a property to the {@link VideoCapture}.
	 * 
	 * @param propId id of the property
	 * @param value the value of the property
	 * @see VideoCapture#set(int, double)
	 */
	public void setCaptureProperty(int propId, double value){
		capture.set(propId, value);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the video capture property of {@link Videoio#CAP_PROP_FPS}
	 * </p>
	 * @see #setCaptureProperty(int, double)
	 * @throws IllegalArgumentException if fps is smaller than 10 or larger than 60
	 */
	@Override
	public void setFPS(int fps) {
		if(fps < 10 || fps > 60)
			throw new IllegalArgumentException("FPS value is not valid! [10..60]");
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
		this.quality = quality;
		compressParams.put(0, 0, new int[]{Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality});
	}
	/**
	 * Gets the frame height.
	 * <p>
	 * Gets the capture property of {@link Videoio#CAP_PROP_FRAME_HEIGHT}
	 * </p>
	 * @return the frame height
	 * @see #getCaptureProperty(int)
	 */
	public int height(){
		return (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
	}
	/**
	 * Gets the frame width.
	 * <p>
	 * Gets the capture property of {@link Videoio#CAP_PROP_FRAME_WIDTH}
	 * </p>
	 * @return the frame width
	 * @see #getCaptureProperty(int)
	 */
	public int width(){
		return (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
	}
	/**
	 * Sets the frame width and height.
	 * <p>
	 * Sets the capture property of {@link Videoio#CAP_PROP_FRAME_WIDTH}
	 * and {@link Videoio#CAP_PROP_FRAME_HEIGHT}.
	 * </p>
	 * @param width the frame width
	 * @param height the frame height
	 * @see #setCaptureProperty(int, double)
	 */
	public void setSize(int width, int height){
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
	}
	
	/**
	 * Releases the camera. Doing so will close the camera and will not enable
	 * usage of it. 
	 * 
	 * @throws IllegalStateException if the camera was already closed
	 */
	public void release(){
		if(!capture.isOpened())
			throw new IllegalStateException("Camera is already closed and cannot be released");
		capture.release();
	}
}
