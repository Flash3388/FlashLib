package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.devices.Encoder;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.devices.RangeFinder;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Vision;

/**
 * PID source is an interface for feedback data to the PID control loop. It is used to determine the current error
 * to be fixed by the loop.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see PIDController
 */
public interface PIDSource {
	
	public static class GyroPIDSource implements PIDSource{

		private Gyro gyro;
		
		public GyroPIDSource(Gyro gyro){
			this.gyro = gyro;
		}
		
		public void setGyro(Gyro gyro){
			this.gyro = gyro;
		}
		public Gyro getGyro(){
			return gyro;
		}
		
		@Override
		public double pidGet() {
			return gyro.getAngle();
		}
	}
	public static class VisionPIDSource implements PIDSource{

		private Vision vision;
		private double previous = 0.0;
		private boolean horizontal, distance;
		
		public VisionPIDSource(Vision vision, boolean horizontal, boolean distance){
			this.vision = vision;
			this.horizontal = horizontal;
			this.distance = distance;
		}
		
		public void setVision(Vision vision){
			this.vision = vision;
		}
		public Vision getVision(){
			return vision;
		}
		
		public void setDistanceMode(boolean distance){
			this.distance = distance;
		}
		public boolean getDistanceMode(){
			return distance;
		}
		public void setHorizontalMode(boolean horizontal){
			this.horizontal = horizontal;
		}
		public boolean getHorizontalMode(){
			return horizontal;
		}
		
		@Override
		public double pidGet() {
			if(!vision.hasNewAnalysis()) return previous;
			if(distance)
				previous = vision.getAnalysis().getDouble(Analysis.PROP_TARGET_DISTANCE);
			else
				previous = horizontal? 
							vision.getAnalysis().getDouble(Analysis.PROP_HORIZONTAL_DISTANCE) : 
							vision.getAnalysis().getDouble(Analysis.PROP_VERTICAL_DISTANCE);
			return previous;
		}
	}
	public static class DoubleSourcePIDSource implements PIDSource{

		private DoubleSource source;
		
		public DoubleSourcePIDSource(DoubleSource source){
			this.source = source;
		}
		
		public void setSource(DoubleSource source){
			this.source = source;
		}
		public DoubleSource getSource(){
			return source;
		}
		
		@Override
		public double pidGet() {
			return source.get();
		}
		
	}
	public static class EncoderPIDSource implements PIDSource{

		private Encoder encoder;
		private boolean useDistance = false;
		
		public EncoderPIDSource(Encoder encoder, boolean useDistance) {
			this.encoder = encoder;
			this.useDistance = useDistance;
		}
		
		public void setUsingDistance(boolean useDistance){
			this.useDistance = useDistance;
		}
		public boolean isUsingDistance(){
			return useDistance;
		}
		
		public void setEncoder(Encoder encoder){
			this.encoder = encoder;
		}
		public Encoder getEncoder(){
			return encoder;
		}
		
		@Override
		public double pidGet() {
			return useDistance? encoder.getDistance() : encoder.getRate();
		}
		
	}
	public static class RangeFinderPIDSource implements PIDSource{

		private RangeFinder rangeFinder;
		
		public RangeFinderPIDSource(RangeFinder rangeFinder) {
			this.rangeFinder = rangeFinder;
		}
		
		public RangeFinder getRangeFinder(){
			return rangeFinder;
		}
		public void setRangeFinder(RangeFinder rangeFinder){
			this.rangeFinder = rangeFinder;
		}
		
		@Override
		public double pidGet() {
			return rangeFinder.getRangeCM();
		}
	}
	
	/**
	 * Gets the feedback data of the sensor. Provides error data to the control loop.
	 * @return the feedback data from the sensor.
	 */
	public double pidGet();
}
