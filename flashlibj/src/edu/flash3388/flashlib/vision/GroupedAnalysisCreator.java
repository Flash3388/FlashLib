package edu.flash3388.flashlib.vision;

import java.util.List;
import java.util.Map;

/**
 * This creator provides the maximum data available for {@link Analysis}. It can work with a group of
 * contours as well as one. When a group is provided, it is considered as one contour and all data is averaged
 * about all the contours.
 * <p>
 * The following parameters are required:
 * <ul>
 * 		<li> target-width: the width of the target contour in real-life. (Measurement units are user dependent)</li>
 * 		<li> target-height: the height of the target contour in real-life. (Measurement units are user dependent)</li>
 * 		<li> cam-fov: angle of view of the camera in radians</li>
 * 		<li> distance-height: true to use height ratio for distance measurement, false for width</li>
 * </ul>
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class GroupedAnalysisCreator implements AnalysisCreator{

	private double targetWidth, targetHeight;
	private double camFov;
	private boolean distanceHeight;
	
	public GroupedAnalysisCreator(){}
	public GroupedAnalysisCreator(double targetWidth, double targetHeight, double camFov, boolean distanceHeight){
		this.targetHeight = targetHeight;
		this.targetWidth = targetWidth;
		this.camFov = camFov;
		this.distanceHeight = distanceHeight;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis createAnalysis(VisionSource source, List<Contour> contours) {
		double centerX = 0, centerY = 0;
		double sumDimension = 0;
		for (Contour contour : contours){
			sumDimension += distanceHeight? contour.getHeight() : contour.getWidth();
			centerX += contour.getX();
			centerY += contour.getY();
		}
		
		Analysis analysis = new Analysis();
		analysis.centerPointX = (int) centerX;
		analysis.centerPointY = (int) centerY;
		
		analysis.verticalDistance = (int) (centerX - source.getFrameHeight() * 0.5);
		analysis.horizontalDistance = (int) (centerY - source.getFrameWidth() * 0.5);
		
		analysis.offsetAngle = VisionUtils.calculateHorizontalOffset(source.getFrameWidth(), source.getFrameHeight(), 
				centerX, centerY, camFov);
		analysis.targetDistance = distanceHeight? 
				VisionUtils.measureDistance(source.getFrameHeight(), sumDimension, targetHeight, 
						camFov)  : 
			VisionUtils.measureDistance(source.getFrameWidth(), sumDimension, targetWidth, 
					camFov);
				
		analysis.pixelsToRealRatio = (distanceHeight? targetHeight : targetWidth) / sumDimension;
		
		return analysis;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parseParameters(Map<String, VisionParam> parameters) {
		targetWidth = VisionParam.getDoubleValue(parameters.get("target-width"));
		targetHeight = VisionParam.getDoubleValue(parameters.get("target-height"));
		camFov = VisionParam.getDoubleValue(parameters.get("cam-fov"));
		distanceHeight = VisionParam.getBooleanValue(parameters.get("distance-height"));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisionParam[] getParameters() {
		return new VisionParam[]{
				new VisionParam.DoubleParam("target-width", targetWidth),
				new VisionParam.DoubleParam("target-height", targetHeight),
				new VisionParam.DoubleParam("cam-fov", camFov),
				new VisionParam.BooleanParam("distance-height", distanceHeight)
		};
	}
}
