package edu.flash3388.flashlib.vision;

public class ClosestToCenterXFilter extends ProcessingFilter{
	public ClosestToCenterXFilter(){}
	@Override
	public void process(VisionSource source) {
		source.closestToCenterX();
	}
	@Override
	public void parseParameters(double[] parameters) {
	}
	@Override
	public double[] getParameters() {
		return new double[0];
	}
}
