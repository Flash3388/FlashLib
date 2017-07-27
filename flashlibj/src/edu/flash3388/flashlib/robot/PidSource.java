package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.vision.Vision;

/**
 * PID source is an interface for feedback data to the PID control loop. It is used to determine the current error
 * to be fixed by the loop.
 * 
 * <p>
 * There are 2 types of feedback data: 
 * <ul>
 * 	<li> {@link PidType#Displacement}: position or rotation</li>
 * 	<li> {@link PidType#Rate}: speed of position or rotation</li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see PidController
 */
public interface PidSource {
	
	public static class GyroPidSource implements PidSource{

		private PidType type;
		private Gyro gyro;
		
		public GyroPidSource(Gyro gyro, PidType t){
			this.gyro = gyro;
			this.type = t;
		}
		public GyroPidSource(Gyro gyro){
			this(gyro, PidType.Displacement);
		}
		
		public void setGyro(Gyro gyro){
			this.gyro = gyro;
		}
		public Gyro getGyro(){
			return gyro;
		}
		public void setType(PidType type){
			this.type = type;
		}
		
		@Override
		public double pidGet() {
			return gyro.getAngle();
		}
		@Override
		public PidType getType() {
			return type;
		}
	}
	public static class VisionPidSource implements PidSource{

		private PidType type;
		private Vision vision;
		private double previous = 0.0;
		private boolean horizontal, distance;
		
		public VisionPidSource(Vision vision, PidType t, boolean horizontal, boolean distance){
			this.vision = vision;
			this.horizontal = horizontal;
			this.distance = distance;
			this.type = t;
		}
		public VisionPidSource(Vision vision, boolean horizontal, boolean distance){
			this(vision, PidType.Displacement, horizontal, distance);
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
				previous = vision.getAnalysis().targetDistance;
			else
				previous = horizontal? 
							vision.getAnalysis().horizontalDistance : 
							vision.getAnalysis().verticalDistance;
			return previous;
		}
		@Override
		public PidType getType() {
			return type;
		}
	}
	public static class DoubleDataPidSource implements PidSource{

		private PidType type;
		private DoubleDataSource source;
		
		public DoubleDataPidSource(DoubleDataSource source, PidType t){
			this.source = source;
			this.type = t;
		}
		public DoubleDataPidSource(DoubleDataSource source){
			this(source, PidType.Displacement);
		}
		
		public void setSource(DoubleDataSource source){
			this.source = source;
		}
		public DoubleDataSource getSource(){
			return source;
		}
		public void setType(PidType type){
			this.type = type;
		}
		
		@Override
		public double pidGet() {
			return source.get();
		}
		@Override
		public PidType getType() {
			return type;
		}
		
	}
	
	/**
	 * Gets the feedback data of the sensor. Provides error data to the control loop.
	 * @return the feedback data from the sensor.
	 */
	public double pidGet();
	/**
	 * Gets the type of data returned by the sensor.
	 * <p>
	 * There are 2 types of feedback data: 
	 * <ul>
	 * 	<li> {@link PidType#Displacement}: position or rotation</li>
	 * 	<li> {@link PidType#Rate}: speed of position or rotation</li>
	 * </ul>
	 * @return the sensor data type
	 */
	public PidType getType();
}
