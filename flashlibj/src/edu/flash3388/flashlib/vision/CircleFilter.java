package edu.flash3388.flashlib.vision;

import java.util.Map;

public class CircleFilter extends ProcessingFilter {

	@Override
	public void process(VisionSource source) {
		source.circleDetection();
		
		
	}

	@Override
	public void parseParameters(Map<String, FilterParam> parameters) {}
	@Override
	public FilterParam[] getParameters() {
		return null;
	}

}
