package edu.flash3388.flashlib.vision;

import java.util.List;

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
public class GroupedAnalysisCreator implements AnalysisCreator {

	/**
	 * Indicates the real life target's width.
	 */
	private double targetWidth;
	/**
	 * Indicates the real life target's height.
	 */
	private double targetHeight;
	/**
	 * Indicates the camera's field of view in radians.
	 */
	private double camFov;
	
	/**
	 * Indicates whether to use height ratios to calculate distance or width rations.
	 */
	private boolean calcDistanceWithHeightRatio;
	
	public GroupedAnalysisCreator(){}
	public GroupedAnalysisCreator(double targetWidth, double targetHeight, double camFov, 
			boolean calcDistanceWithHeightRatio){
		this.targetHeight = targetHeight;
		this.targetWidth = targetWidth;
		this.camFov = camFov;
		this.calcDistanceWithHeightRatio = calcDistanceWithHeightRatio;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis createAnalysis(VisionSource source) {
		List<Contour> contours = source.getContours();
		
		double centerX = 0, centerY = 0;
		double sumDimension = 0;
		for (Contour contour : contours){
			sumDimension += calcDistanceWithHeightRatio? contour.getHeight() : contour.getWidth();
			centerX += contour.getX();
			centerY += contour.getY();
		}
		centerX /= contours.size();
		centerY /= contours.size();
		
		Analysis analysis = new Analysis();
		
		analysis.setDouble(Analysis.PROP_CENTER_X, centerX);
		analysis.setDouble(Analysis.PROP_CENTER_Y, centerY);
		analysis.setDouble(Analysis.PROP_HORIZONTAL_DISTANCE, (centerX - source.getFrameWidth() * 0.5));
		analysis.setDouble(Analysis.PROP_VERTICAL_DISTANCE, (centerY - source.getFrameHeight() * 0.5));
		analysis.setDouble(Analysis.PROP_ANGLE_OFFSET, 
				VisionUtils.calculateHorizontalOffset(source.getFrameWidth(), source.getFrameHeight(), 
				centerX, centerY, camFov));
		analysis.setDouble(Analysis.PROP_TARGET_DISTANCE, 
				calcDistanceWithHeightRatio? 
				VisionUtils.measureDistance(source.getFrameHeight(), sumDimension, targetHeight, 
						camFov)  : 
			VisionUtils.measureDistance(source.getFrameWidth(), sumDimension, targetWidth, 
					camFov));
		
		return analysis;
	}
}
