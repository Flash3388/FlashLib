package edu.flash3388.flashlib.vision;

import java.util.List;

import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.SimpleBooleanProperty;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;

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

	private DoubleProperty targetWidth = new SimpleDoubleProperty(), 
			targetHeight = new SimpleDoubleProperty(),
			camFov = new SimpleDoubleProperty();
	private BooleanProperty distanceHeight = new SimpleBooleanProperty();
	
	public GroupedAnalysisCreator(){}
	public GroupedAnalysisCreator(double targetWidth, double targetHeight, double camFov, boolean distanceHeight){
		this.targetHeight.set(targetHeight);
		this.targetWidth.set(targetWidth);
		this.camFov.set(camFov);
		this.distanceHeight.set(distanceHeight);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis createAnalysis(VisionSource source, List<Contour> contours) {
		double centerX = 0, centerY = 0;
		double sumDimension = 0;
		for (Contour contour : contours){
			sumDimension += distanceHeight.get()? contour.getHeight() : contour.getWidth();
			centerX += contour.getX();
			centerY += contour.getY();
		}
		
		Analysis analysis = new Analysis();
		analysis.centerPointX = (int) centerX;
		analysis.centerPointY = (int) centerY;
		
		analysis.verticalDistance = (int) (centerX - source.getFrameHeight() * 0.5);
		analysis.horizontalDistance = (int) (centerY - source.getFrameWidth() * 0.5);
		
		analysis.offsetAngle = VisionUtils.calculateHorizontalOffset(source.getFrameWidth(), source.getFrameHeight(), 
				centerX, centerY, camFov.get());
		analysis.targetDistance = distanceHeight.get()? 
				VisionUtils.measureDistance(source.getFrameHeight(), sumDimension, targetHeight.get(), 
						camFov.get())  : 
			VisionUtils.measureDistance(source.getFrameWidth(), sumDimension, targetWidth.get(), 
					camFov.get());
				
		analysis.pixelsToRealRatio = (distanceHeight.get()? targetHeight.get() : targetWidth.get()) / sumDimension;
		
		return analysis;
	}
}
