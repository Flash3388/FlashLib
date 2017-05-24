package edu.flash3388.flashlib.vision;

public class ClosestToCenterYFilter extends ProcessingFilter{
	public ClosestToCenterYFilter(){}
	@Override
	public void process(VisionSource source) {
		source.closestToCenterY();
	}
	@Override
	public void parseParameters(double[] parameters) {
	}
	@Override
	public double[] getParameters() {
		return new double[0];
	}
}
