package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;


/**
 * Filers out contours using ratio filtering. Designed to locate to contours with specific position and side ratios.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#contourRatio(double, double, double, double, double, double, double, double, double, double)
 */
public class RatioFilter extends VisionFilter{
	
	private DoubleProperty heightRatio = new SimpleDoubleProperty();
	private DoubleProperty widthRatio = new SimpleDoubleProperty();
	private DoubleProperty dy = new SimpleDoubleProperty();
	private DoubleProperty dx = new SimpleDoubleProperty();
	private DoubleProperty maxScore = new SimpleDoubleProperty();
	private DoubleProperty minScore = new SimpleDoubleProperty();
	private DoubleProperty maxHeight = new SimpleDoubleProperty();
	private DoubleProperty minHeight = new SimpleDoubleProperty();
	private DoubleProperty maxWidth = new SimpleDoubleProperty();
	private DoubleProperty minWidth = new SimpleDoubleProperty();

	public RatioFilter(){}
	public RatioFilter(double heightRatio, double widthRatio, double dy, double dx, double maxScore, double minScore, 
			double maxHeight, double minHeight, double maxWidth, double minWidth){
		this.maxScore.set(maxScore);
		this.dx.set(dx);
		this.dy.set(dy);
		this.heightRatio.set(heightRatio);
		this.maxHeight.set(maxHeight);
		this.maxWidth.set(maxWidth);
		this.minHeight.set(minHeight);
		this.minScore.set(minScore);
		this.minWidth.set(minWidth);
		this.widthRatio.set(widthRatio);
	}
	
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the height ratio between the first contour and the second contour.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty heightRatioProperty(){
		return heightRatio;
	}
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the width ratio between the first contour and the second contour.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty widthRatioProperty(){
		return widthRatio;
	}
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the x axis positioning ratio between the first contour and the second contour. This indicates the
	 * ratio between the x distance of the right edge of the second contour and the left edge of the first to the
	 * width of the first contour.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty dxProperty(){
		return dx;
	}
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the y axis positioning ratio between the first contour and the second contour. This indicates the
	 * ratio between the y distance of the bottom edge of the second contour and the top edge of the first to the
	 * width of the first contour.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty dyProperty(){
		return dy;
	}
	
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the worst possible value of a ratio result. When the best result is larger than the worst, it will be 
	 * discarded.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty maxScoreProperty(){
		return maxScore;
	}
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the best possible value of a ratio result. When a result is smaller than the best, it will be 
	 * Immediately regarded as the best.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty minScoreProperty(){
		return minScore;
	}
	
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the maximum possible height of a contour. Contours whose height is bigger than this value will be
	 * ignored.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty maxHeightProperty(){
		return maxHeight;
	}
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the minimum possible height of a contour. Contours whose height is smaller than this value will be
	 * ignored.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty minHeightProperty(){
		return minHeight;
	}
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the maximum possible width of a contour. Contours whose width is bigger than this value will be
	 * ignored.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty maxWidthProperty(){
		return maxWidth;
	}
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the minimum possible width of a contour. Contours whose width is smaller than this value will be
	 * ignored.
	 * Must be non-negative
	 * @return the property
	 */
	public DoubleProperty minWidthProperty(){
		return minWidth;
	}
	
	@Override
	public void process(VisionSource source) {
		source.contourRatio(heightRatio.get(), widthRatio.get(), dy.get(), dx.get(), maxScore.get(), minScore.get(), 
				maxHeight.get(), minHeight.get(), maxWidth.get(), minWidth.get());
	}
}
