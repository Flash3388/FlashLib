package edu.flash3388.flashlib.vision;

import java.io.File;
import java.util.ArrayList;

import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.util.beans.SimpleBooleanProperty;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;
import edu.flash3388.flashlib.util.beans.ValueSource;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleProperty;

/**
 * Filters for a part of the image which matches a given template.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class TemplateAnalysisCreator implements AnalysisCreator{

	private TemplateMatcher matcher;
	
	private DoubleProperty scaleFactor = new SimpleDoubleProperty();
	private IntegerProperty method = new SimpleIntegerProperty();
	private Property<String> imgDirPath = new SimpleProperty<String>();
	
	private DoubleProperty targetWidth = new SimpleDoubleProperty(), 
			targetHeight = new SimpleDoubleProperty(),
			camFov = new SimpleDoubleProperty();
	private BooleanProperty distanceHeight = new SimpleBooleanProperty();
	
	public TemplateAnalysisCreator(){}
	public TemplateAnalysisCreator(String imgDirPath, int method, double scaleFactor, 
			double targetWidth, double targetHeight, double camFov, boolean distanceHeight){
		this.imgDirPath.setValue(imgDirPath);
		this.scaleFactor.set(scaleFactor);
		this.method.set(method);
		
		this.targetHeight.set(targetHeight);
		this.targetWidth.set(targetWidth);
		this.camFov.set(camFov);
		this.distanceHeight.set(distanceHeight);
	}
	
	/**
	 * An {@link DoubleProperty}.
	 * Indicates the real life target's width.
	 * @return the property
	 */
	public DoubleProperty targetWidthProperty(){
		return targetWidth;
	}
	/**
	 * An {@link DoubleProperty}.
	 * Indicates the real life target's height.
	 * @return the property
	 */
	public DoubleProperty targetHeightProperty(){
		return targetHeight;
	}
	/**
	 * An {@link DoubleProperty}.
	 * Indicates the camera's field of view in radians.
	 * @return the property
	 */
	public DoubleProperty camFovProperty(){
		return camFov;
	}
	/**
	 * An {@link BooleanProperty}.
	 * Indicates whether to use height ratios to calculate distance or width rations.
	 * @return the property
	 */
	public BooleanProperty distanceHeightProperty(){
		return distanceHeight;
	}
	
	/**
	 * A {@link DoubleProperty}.
	 * Indicates the scale factor used to resize the image per iteration.
	 * @return the property
	 */
	public DoubleProperty scaleFactorProperty(){
		return scaleFactor;
	}
	/**
	 * An {@link IntegerProperty}.
	 * Indicates the method used to perform the template matching with.
	 * Must be non-negative
	 * @return the property
	 */
	public IntegerProperty methodProperty(){
		return method;
	}
	/**
	 * A {@link Property} using a String type.
	 * Indicates the path to a folder containing the template images. All images in that folder will be loaded as
	 * templates.
	 * @return the property
	 */
	public Property<String> imageDirectoryPathProperty(){
		return imgDirPath;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Analysis createAnalysis(VisionSource source) {
		if(matcher == null){
			ValueSource<Object>[] imgs = null;
			
			File dir = new File(imgDirPath.getValue());
			if(!dir.exists() || !dir.isDirectory())
				return null;
			File[] files = dir.listFiles();
			ArrayList<ValueSource<Object>> imgList = new ArrayList<ValueSource<Object>>();
			for (int i = 0; i < files.length; i++) {
				ValueSource<Object> img = source.loadImage(files[i].getAbsolutePath(), true);
				if(img != null && img.getValue() != null)
					imgList.add(img);
			}
			imgs = new ValueSource[imgList.size()];
			imgList.toArray(imgs);
			
			matcher = source.createTemplateMatcher(imgs, method.get());
		}
		
		MatchResult result = source.matchTemplate(matcher, scaleFactor.get());
		return setUpAnalysis(source, result);
		
	}
	private Analysis setUpAnalysis(VisionSource source, MatchResult result) {
		Analysis analysis = new Analysis();
		analysis.setDouble(Analysis.PROP_CENTER_X, result.centerx);
		analysis.setDouble(Analysis.PROP_CENTER_Y, result.centery);
		analysis.setDouble(Analysis.PROP_HORIZONTAL_DISTANCE, (result.centerx - source.getFrameWidth() * 0.5));
		analysis.setDouble(Analysis.PROP_VERTICAL_DISTANCE, (result.centery - source.getFrameHeight() * 0.5));
		analysis.setDouble(Analysis.PROP_ANGLE_OFFSET, 
				VisionUtils.calculateHorizontalOffset(source.getFrameWidth(), source.getFrameHeight(), 
						result.centerx , result.centery , camFov.get()));
		analysis.setDouble(Analysis.PROP_TARGET_DISTANCE, 
				distanceHeight.get()? 
				VisionUtils.measureDistance(source.getFrameHeight(), source.getFrameHeight(), targetHeight.get(), 
						camFov.get())  : 
			VisionUtils.measureDistance(source.getFrameWidth(), source.getFrameWidth(), targetWidth.get(), 
					camFov.get()));
		
		return analysis;
	}
}
