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
	
	public DoubleProperty heightRatioProperty(){
		return heightRatio;
	}
	public DoubleProperty widthRatioProperty(){
		return widthRatio;
	}
	public DoubleProperty dxProperty(){
		return dx;
	}
	public DoubleProperty dyProperty(){
		return dy;
	}
	
	public DoubleProperty maxScoreProperty(){
		return maxScore;
	}
	public DoubleProperty minScoreProperty(){
		return minScore;
	}
	
	public DoubleProperty maxHeightProperty(){
		return maxHeight;
	}
	public DoubleProperty minHeightProperty(){
		return minHeight;
	}
	public DoubleProperty maxWidthProperty(){
		return maxWidth;
	}
	public DoubleProperty minWidthProperty(){
		return minWidth;
	}
	
	@Override
	public void process(VisionSource source) {
		source.contourRatio(heightRatio.get(), widthRatio.get(), dy.get(), dx.get(), maxScore.get(), minScore.get(), 
				maxHeight.get(), minHeight.get(), maxWidth.get(), minWidth.get());
	}
}
