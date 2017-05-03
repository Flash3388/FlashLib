package edu.flash3388.flashlib.vision;

import org.opencv.core.Mat;

public class CvRunner extends VisionRunner implements ImagePipeline{

	private Mat[] frames = new Mat[2];
	private int frameIndex = 0;
	
	public CvRunner(String name, int id) {
		super(name, id);
	}
	public CvRunner(String name){
		super(name);
	}
	public CvRunner(){
		super();
	}

	@Override
	protected Analysis analyse() {
		Mat mat = frames[frameIndex];
		frames[frameIndex] = null;
		frameIndex ^= 1;
		if(mat != null)
			return CvProcessing.analyseImage(mat, getParameters());
		return null;
	}

	@Override
	public void newImage(Mat mat, int type) {
		frames[1-frameIndex] = mat;
	}
}
