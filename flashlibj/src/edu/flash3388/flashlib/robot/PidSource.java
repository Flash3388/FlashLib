package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Vision;

/**
 * PID source is an interface for feedback data to the PID control loop. It is used to determine the current error
 * to be fixed by the loop.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see PidController
 */
public interface PidSource {
	
	public static class GyroPidSource implements PidSource{

		private Gyro gyro;
		
		public GyroPidSource(Gyro gyro){
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
	public static class VisionPidSource implements PidSource{

		private Vision vision;
		private double previous = 0.0;
		private boolean horizontal, distance;
		
		public VisionPidSource(Vision vision, boolean horizontal, boolean distance){
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
	public static class DoubleDataPidSource implements PidSource{

		private DoubleSource source;
		
		public DoubleDataPidSource(DoubleSource source){
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
	
	/**
	 * Gets the feedback data of the sensor. Provides error data to the control loop.
	 * @return the feedback data from the sensor.
	 */
	public double pidGet();
}
