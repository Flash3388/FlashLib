package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.math.Vector2;
import edu.flash3388.flashlib.math.Vector3;

/**
 * Provides utilities for performing vision actions. Holds methods for calculations mostly.
 * 
 * @author Tom Tzook
 * @author Alon Klein
 * @since FlashLib 1.0.1
 */
public class VisionUtils {
	private VisionUtils(){}
	
	/**
	 * Gets the center of a group of contours.
	 * 
	 * @param contours array of contours
	 * @return center of the contours
	 */
	public static Vector2 contourCenter(Contour...contours){
		Vector2 vec = new Vector2();
		for (Contour contour : contours)
			vec.addSelf(contour.getX(), contour.getY());
		vec.divSelf(contours.length);
		return vec;
	}
	
	/**
	 * Calculates horizontal distance from the camera to the contour using ratio between real life width and in-image
	 * width or height. Whether width or height is used should be consistent throughout the parameters.
	 * 
	 * @param imgDim width or height of the image
	 * @param actualDim the real life expected width or height the contour
	 * @param contourDim the contour width or height measured in the image
	 * @param angleOfView angle of view of the camera in radians
	 * @return estimated distance from object. Measurement units depends on the once used in actualWidth.
	 */
	public static double measureDistance(double imgDim, double contourDim, double actualDim, 
			double angleOfView){
		return (actualDim * imgDim / (2 * contourDim * Math.tan(angleOfView)));
	}
	
	/**
	 * Calculates horizontal angle from the focal to a pixel.
	 * 
	 * @param imgWidth width of the image
	 * @param imgHeight height of the image
	 * @param px x coordinate to calculate offset to
	 * @param py y coordinate to calculate offset to
	 * @param fovDegrees angle of view of the camera in degrees
	 * @return the offset of an object from the center in degrees
	 */
	public static double calculateHorizontalOffsetInDegrees(double imgWidth, double imgHeight, double px, double py, double fovDegrees){
		// Compute focal length in pixels from FOV
		return Math.toDegrees(calculateHorizontalOffset(imgWidth, imgHeight, px, py, Math.toRadians(fovDegrees)));
	}
	/**
	 * Calculates horizontal angle from the focal to a pixel.
	 * 
	 * @param imgWidth width of the image
	 * @param imgHeight height of the image
	 * @param px x coordinate to calculate offset to
	 * @param py y coordinate to calculate offset to
	 * @param fovRadians angle of view of the camera in radians
	 * @return the offset of an object from the center in radians
	 */
	public static double calculateHorizontalOffset(double imgWidth, double imgHeight, double px, double py, double fovRadians){
		// Compute focal length in pixels from FOV
		double center_x = imgWidth * 0.5;
		double center_y = imgHeight * 0.5;
		double f = center_x / Math.tan(0.5 * fovRadians);
		// Vectors subtending image center and pixel from optical center
		// in camera coordinates.
		Vector3 center = new Vector3(0, 0, f);		
		Vector3 pixel = new Vector3(px - center_x, py - center_y, f);

		// angle between vector (0, 0, f) and pixel
		return center.angleTo(pixel);
	}
}
