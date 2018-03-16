package edu.flash3388.flashlib.vision;

import java.io.File;
import java.util.ArrayList;

import edu.flash3388.flashlib.util.beans.ValueSource;

/**
 * Filters for a part of the image which matches a given template.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class TemplateAnalysisCreator implements AnalysisCreator {

	private TemplateMatcher matcher;
	
	/**
	 * Indicates the scale factor used to resize the image per iteration.
	 */
	private double scaleFactor;
	/**
	 * Indicates the method used to perform the template matching with.
	 * Must be non-negative.
	 */
	private int method;
	/**
	 * Indicates the path to a folder containing the template images. All images in that folder will be loaded as
	 * templates.
	 */
	private String imgDirPath;
	
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
	/**
	 * Indicates whether to load template images as binaries or not.
	 */
	private boolean loadBinary;
	
	public TemplateAnalysisCreator(){}
	public TemplateAnalysisCreator(String imgDirPath, int method, double scaleFactor, 
			double targetWidth, double targetHeight, double camFov, 
			boolean calcDistanceWithHeightRatio, boolean loadBinary){
		this.imgDirPath = imgDirPath;
		this.scaleFactor = scaleFactor;
		this.method = method;
		
		this.targetHeight = targetHeight;
		this.targetWidth = targetWidth;
		this.camFov = camFov;
		this.calcDistanceWithHeightRatio = calcDistanceWithHeightRatio;
		this.loadBinary = loadBinary;
	}
	
	@Override
	public Analysis createAnalysis(VisionSource source) {
		if(matcher == null){
			Object[] imgs = null;
			
			File dir = new File(imgDirPath);
			if(!dir.exists() || !dir.isDirectory())
				return null;
			File[] files = dir.listFiles();
			ArrayList<Object> imgList = new ArrayList<Object>();
			for (int i = 0; i < files.length; i++) {
				Object img = source.loadImage(files[i].getAbsolutePath(), loadBinary);
				if(img != null)
					imgList.add(img);
			}
			imgs = new ValueSource[imgList.size()];
			imgList.toArray(imgs);
			
			matcher = source.createTemplateMatcher(imgs, method);
		}
		
		MatchResult result = source.matchTemplate(matcher, scaleFactor);
		return setUpAnalysis(source, result);
		
	}
	private Analysis setUpAnalysis(VisionSource source, MatchResult result) {
		if(result == null)
			return null;
		Analysis analysis = new Analysis();
		analysis.setDouble(Analysis.PROP_CENTER_X, result.centerx);
		analysis.setDouble(Analysis.PROP_CENTER_Y, result.centery);
		analysis.setDouble(Analysis.PROP_HORIZONTAL_DISTANCE, (result.centerx - source.getFrameWidth() * 0.5));
		analysis.setDouble(Analysis.PROP_VERTICAL_DISTANCE, (result.centery - source.getFrameHeight() * 0.5));
		analysis.setDouble(Analysis.PROP_ANGLE_OFFSET, 
				VisionUtils.calculateHorizontalOffset(source.getFrameWidth(), source.getFrameHeight(), 
						result.centerx , result.centery , camFov));
		analysis.setDouble(Analysis.PROP_TARGET_DISTANCE, 
				calcDistanceWithHeightRatio? 
				VisionUtils.measureDistance(source.getFrameHeight(), source.getFrameHeight(), targetHeight, 
						camFov)  : 
			VisionUtils.measureDistance(source.getFrameWidth(), source.getFrameWidth(), targetWidth, 
					camFov));
		
		return analysis;
	}
}
