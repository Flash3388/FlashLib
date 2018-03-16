package edu.flash3388.flashlib.vision;

/**
 * Filers out contours using ratio filtering. Designed to locate to contours with specific position and side ratios.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#contourRatio(double, double, double, double, double, double, double, double, double, double)
 */
public class RatioFilter extends VisionFilter{
	
	/**
	 * Indicates the height ratio between the first contour and the second contour.
	 * Must be non-negative.
	 */
	private double heightRatio;
	/**
	 * Indicates the width ratio between the first contour and the second contour.
	 * Must be non-negative.
	 */
	private double widthRatio;
	
	/**
	 * Indicates the x axis positioning ratio between the first contour and the second contour. This indicates the
	 * ratio between the x distance of the right edge of the second contour and the left edge of the first to the
	 * width of the first contour.
	 * Must be non-negative.
	 */
	private double dx;
	/**
	 * Indicates the y axis positioning ratio between the first contour and the second contour. This indicates the
	 * ratio between the y distance of the bottom edge of the second contour and the top edge of the first to the
	 * width of the first contour.
	 * Must be non-negative.
	 */
	private double dy;
	
	/**
	 * Indicates the worst possible value of a ratio result. When the best result is larger than the worst, it will be 
	 * discarded.
	 * Must be non-negative.
	 */
	private double maxScore;
	/**
	 * Indicates the best possible value of a ratio result. When a result is smaller than the best, it will be 
	 * Immediately regarded as the best.
	 * Must be non-negative.
	 */
	private double minScore;
	
	/**
	 * Indicates the maximum possible height of a contour. Contours whose height is bigger than this value will be
	 * ignored.
	 * Must be non-negative.
	 */
	private double maxHeight;
	/**
	 * Indicates the minimum possible height of a contour. Contours whose height is smaller than this value will be
	 * ignored.
	 * Must be non-negative.
	 */
	private double minHeight;
	
	/**
	 * Indicates the maximum possible width of a contour. Contours whose width is bigger than this value will be
	 * ignored.
	 * Must be non-negative.
	 */
	private double maxWidth;
	/**
	 * Indicates the minimum possible width of a contour. Contours whose width is smaller than this value will be
	 * ignored.
	 * Must be non-negative.
	 */
	private double minWidth;

	public RatioFilter(){}
	public RatioFilter(double heightRatio, double widthRatio, double dy, double dx, double maxScore, double minScore, 
			double maxHeight, double minHeight, double maxWidth, double minWidth){
		this.maxScore = maxScore;
		this.dx = dx;
		this.dy = dy;
		this.heightRatio = heightRatio;
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.minScore = minScore;
		this.minWidth = minWidth;
		this.widthRatio = widthRatio;
	}
	
	@Override
	public void process(VisionSource source) {
		source.contourRatio(heightRatio, widthRatio, dy, dx, maxScore, minScore, 
				maxHeight, minHeight, maxWidth, minWidth);
	}
}
