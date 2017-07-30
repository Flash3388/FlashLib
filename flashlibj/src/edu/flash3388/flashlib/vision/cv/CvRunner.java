package edu.flash3388.flashlib.vision.cv;

import org.opencv.core.Mat;

import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.VisionRunner;

/**
 * Vision runner using openCV. Uses {@link CvPipeline} to receive new openCV {@link Mat} object to analyze.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionRunner
 */
public class CvRunner extends VisionRunner implements CvPipeline{

	private Mat[] frames = new Mat[2];
	private int frameIndex = 0;
	private CvSource source = new CvSource();
	private CvPipeline pipline;
	
	public CvRunner(String name, int id) {
		super(name, id);
	}
	public CvRunner(String name){
		super(name);
	}
	public CvRunner(){
		super();
	}

	public void setPipeline(CvPipeline pipe){
		pipline = pipe;
		source.setPipeline(pipe);
	}
	
	@Override
	protected Analysis analyse() {
		Mat mat = frames[frameIndex];
		frames[frameIndex] = null;
		frameIndex ^= 1;
		if(mat != null && getProcessing() != null){
			source.prep(mat.clone());
			Analysis an = getProcessing().processAndGet(source);
			if(an != null)
				CvProcessing.drawPostProcessing(mat, an);
			if(pipline != null)
				pipline.newImage(mat, CvPipeline.TYPE_POST_PROCESS);
			return an;
		}
		return null;
	}

	@Override
	public void newImage(Mat mat, byte type) {
		frames[1-frameIndex] = mat;
	}
}
