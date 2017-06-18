package edu.flash3388.flashlib.vision;

import org.opencv.core.Mat;

/**
 * Used to transfer openCV {@link Mat} object.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface CvPipeline {
	public static final byte TYPE_THRESHOLD = 1, TYPE_POST_PROCESS = 2, TYPE_PRE_PROCESS = 3;
	
	void newImage(Mat mat, byte type);
}
