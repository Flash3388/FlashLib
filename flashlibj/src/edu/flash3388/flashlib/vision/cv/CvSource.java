package edu.flash3388.flashlib.vision.cv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;

import edu.flash3388.flashlib.util.beans.SimpleProperty;
import edu.flash3388.flashlib.util.beans.ValueSource;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Contour;
import edu.flash3388.flashlib.vision.ImagePipeline;
import edu.flash3388.flashlib.vision.MatchResult;
import edu.flash3388.flashlib.vision.TemplateMatcher;
import edu.flash3388.flashlib.vision.VisionSource;
import edu.flash3388.flashlib.vision.VisionSourceImageMissingException;
import edu.flash3388.flashlib.vision.cv.CvTemplateMatcher.Method;

/**
 * A vision source using openCV.
 * 
 * @author Tom Tzook
 * @author Alon Klein
 * @since FlashLib 1.0.0
 */
public class CvSource implements VisionSource{
	
	private Mat mat, threshold = new Mat(), contoursHierarchy = new Mat();
	private List<MatOfPoint> contours;
	private Analysis analysis;
	private ImagePipeline pipeline;

	private void checkReady(boolean insureContours, boolean insureThreshold){
		if(mat == null)
			throw new VisionSourceImageMissingException();
		
		if((insureContours || insureThreshold) && threshold == null){
			threshold = new Mat();
			mat.copyTo(threshold);
		}
		if(insureContours && contours == null)
			detectContours();
	}
	private void detectContours(){
		if(pipeline != null)
			pipeline.newImage(threshold, ImagePipeline.TYPE_THRESHOLD);
		
		if(contours == null)
			contours = new ArrayList<MatOfPoint>();
		else
			contours.clear();
		
		CvProcessing.detectContours(threshold, contours, contoursHierarchy);
	}
	
	public Mat getThreshold(){
		return threshold;
	}
	
	@Override
	public void setImagePipeline(ImagePipeline pipeline){
		this.pipeline = pipeline;
	}
	@Override
	public ImagePipeline getImagePipeline(){
		return pipeline;
	}
	@Override
	public void drawAnalysisResult(Object frame, Analysis analysis){
		if(frame instanceof Mat)
			CvProcessing.drawPostProcessing((Mat)frame, analysis);
		else
			throw new IllegalArgumentException("Frame is not compatible with this implementation: cv");
	}
	
	/**
	 * Sets the {@link Mat} object to be used for vision.
	 * @param mat frame
	 */
	public void prep(Mat mat){
		this.mat = mat;
		analysis = null;
		threshold = null;
		contours = null;
	}
	
	@Override
	public void setFrame(Object frame){
		if(frame instanceof Mat)
			prep(((Mat)frame).clone());
		else 
			throw new IllegalArgumentException("Frame is not compatible with this implementation: cv");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFrameWidth() {
		return mat != null? mat.width() : 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFrameHeight() {
		return mat != null? mat.height() : 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Contour> getContours() {
		checkReady(true, true);
		
		List<Contour> wrapperContours = new ArrayList<Contour>();
		for (MatOfPoint contour : contours)
			wrapperContours.add(CvProcessing.wrapCvContour(contour));
		return wrapperContours;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis getResult() {
		if(analysis != null)
			return analysis;
		if(contours.size() != 1)
			return null;
		
		MatOfPoint contour = contours.get(0);
		analysis = new Analysis();
		CvProcessing.setAnalysisForContour(mat, contour, analysis);
		
		return analysis;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis[] getResults() {
		Analysis[] ans = new Analysis[contours.size()];
		
		for (int i = 0; i < ans.length; i++) {
			ans[i] = new Analysis();
			CvProcessing.setAnalysisForContour(mat, contours.get(i), ans[i]);
		}
		return ans;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void convertHsv() {
		checkReady(false, false);
		
		CvProcessing.rgbToHsv(mat, mat);
	}
	/**
	 * {@inheritDoc}
	 * Does nothing at the moment
	 */
	@Override
	public void convertRgb() {
		checkReady(false, false);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void convertGrayscale(){
		checkReady(false, false);
		
		CvProcessing.rgbToGray(mat, mat);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filterColorRange(int min1, int min2, int min3, int max1, int max2, int max3){
		checkReady(false, false);
		
		threshold = new Mat();
		CvProcessing.filterMatColors(mat, threshold, min1, max1, min2, max2, min3, max3);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filterColorRange(int min, int max){
		checkReady(false, false);
		
		CvProcessing.filterMatColors(mat, threshold, min, max, min, max, min, max);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> void filterByComparator(int amount, Comparator<T> comparator) {
		checkReady(true, true);
		
		CvProcessing.filterByComparator(contours, amount, (Comparator<MatOfPoint>) comparator);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void lowestContours(int amount) {
		checkReady(true, true);
		
		CvProcessing.filterForLowestContours(contours, amount);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void highestContours(int amount) {
		checkReady(true, true);
		
		CvProcessing.filterForHighestContours(contours, amount);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void largestContours(int amount) {
		checkReady(true, true);
		
		CvProcessing.filterForLargestContours(contours, amount);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void detectShapes(int amount, int vertecies, double accuracy) {
		checkReady(true, true);
		
		detectShapes(vertecies, accuracy);
		if(amount < contours.size()){
			for (int i = contours.size() - 1; i >= amount; i--)
				contours.remove(i);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void detectShapes(int vertecies, double accuracy) {
		checkReady(true, true);
		
		CvProcessing.detectContoursByShape(contours, vertecies, accuracy);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contourRatio(double heightRatio, double widthRatio, double dy, double dx, 
			double maxScore, double minScore, 
			double maxHeight, double minHeight, double maxWidth, double minWidth) {
		checkReady(true, true);
		
		analysis = CvProcessing.filterForContoursByRatio(contours, mat, heightRatio, widthRatio, dy, dx, 
				maxScore, minScore, maxHeight, minHeight, maxWidth, minWidth);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closestToLeft(int amount) {
		checkReady(true, true);
		
		CvProcessing.filterForClosestToLeft(contours, mat, amount);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closestToRight(int amount) {
		checkReady(true, true);
		
		CvProcessing.filterForClosestToRight(contours, mat, amount);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closestToCoordinate(double x, double y, int amount) {
		checkReady(true, true);
		
		CvProcessing.filterForClosestContoursToPoint(contours, x, y, amount);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closestToCenterFrame(int amount) {
		checkReady(true, true);
		
		CvProcessing.filterForClosestContoursToCenter(contours, mat, amount);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void circleDetection() {
		checkReady(true, true);
		
		CvProcessing.FilterByCircle(threshold,contours);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MatchResult matchTemplate(TemplateMatcher matcher, double scaleFactor) {
		if(matcher == null)
			throw new NullPointerException("Template matcher is null");
		if(!(matcher instanceof CvTemplateMatcher))
			throw new IllegalArgumentException("Template Matcher is not compatible with this vision source");
		
		checkReady(false, true);
		
		MatchResult result = ((CvTemplateMatcher)matcher).match(threshold, scaleFactor);
		
		contours = null;
		return result;
	}
	
	public TemplateMatcher createTemplateMatcher(ValueSource<Object>[] imgs, int method) {
		if(method < 0 || method >= CvTemplateMatcher.Method.values().length)
			throw new ArrayIndexOutOfBoundsException("Method type is out of bounds of available types: "
					+method + ":" + CvTemplateMatcher.Method.values().length);
		
		Mat[] templates = new Mat[imgs.length];
		Object imgData = null;
		for (int i = 0; i < templates.length; i++) {
			imgData = imgs[i].getValue();
			if(imgData == null)
				throw new NullPointerException("template image cannot be null: "+i);
			if(!(imgData instanceof Mat))
				throw new IllegalArgumentException("Image format does not match vision source: "+i);
			templates[i] = (Mat)imgData;
		}
		
		return new CvTemplateMatcher(templates, Method.values()[method]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueSource<Object> loadImage(String imgPath, boolean binary) {
		Mat mat = Imgcodecs.imread(imgPath,binary ? CvType.CV_8UC1 : CvType.CV_8UC3);
		if(mat == null)
			return null;
		return new SimpleProperty<Object>(mat);
	}
}
