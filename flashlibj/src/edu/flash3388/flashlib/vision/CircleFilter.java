package edu.flash3388.flashlib.vision;

/**
 * 
 * Filters out non circle shape in the image.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * @see VisionSource#circleDetection()
 */
public class CircleFilter extends VisionFilter {

	@Override
	public void process(VisionSource source) {
		source.circleDetection();
	}

}
