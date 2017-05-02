package edu.flash3388.flashlib.vision;

import org.opencv.core.Mat;

@FunctionalInterface
public interface ImagePipeline {
	public static int TYPE_THRESHOLD = 1, TYPE_POST_PROCESS = 2, TYPE_PRE_PROCESS = 3;
	
	void newImage(Mat mat, int type);
}
