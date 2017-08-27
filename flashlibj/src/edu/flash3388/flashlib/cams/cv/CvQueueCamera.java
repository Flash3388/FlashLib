package edu.flash3388.flashlib.cams.cv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import edu.flash3388.flashlib.cams.QueueCamera;

/**
 * Implementation of {@link QueueCamera} for opencv {@link Mat}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class CvQueueCamera extends QueueCamera<Mat>{

	private MatOfByte buffer;
	private MatOfInt compressParams;
	
	private int fps, quality;
	
	public CvQueueCamera(int fps, int quality){
		buffer = new MatOfByte();
		compressParams = new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality);
		this.fps = fps;
		this.quality = quality;
	}
	public CvQueueCamera(){
		this(30, 100);
	}
	
	@Override
	public byte[] getData() {
		Mat m = dequeue();
		if(m == null)
			return null;
		Imgcodecs.imencode(".jpg", m, buffer, compressParams);
		byte[] imageArr = new byte[(int) (buffer.total() * buffer.elemSize())];
		buffer.get(0, 0, imageArr);
		return imageArr;
	}

	@Override
	public int getQuality() {
		return quality;
	}

	@Override
	public int getFPS() {
		return fps;
	}
	@Override
	public void setFPS(int fps) {
		this.fps = fps;
	}

	@Override
	public void setQuality(int quality) {
		if(quality < 1 || quality > 100)
			throw new IllegalArgumentException("Quality value is not value! [1..100]");
		this.quality = quality;
		compressParams.put(0, 0, new int[]{Imgcodecs.CV_IMWRITE_JPEG_QUALITY, quality});
	}
}
