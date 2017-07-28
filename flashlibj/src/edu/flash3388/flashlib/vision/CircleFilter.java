package edu.flash3388.flashlib.vision;

public class CircleFilter extends VisionFilter {

	@Override
	public void process(VisionSource source) {
		source.circleDetection();
	}

}
