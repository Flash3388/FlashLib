package edu.flash3388.flashlib.robot.systems;

import java.util.Arrays;

import edu.flash3388.flashlib.math.Vector2;
import edu.flash3388.flashlib.robot.ScheduledTask;
import edu.flash3388.flashlib.robot.devices.RangeFinder;
import edu.flash3388.flashlib.robot.devices.Servo;
import edu.flash3388.flashlib.util.FlashUtil;

public class RotatableRadarSystem {

	public static class ScanningTask implements ScheduledTask{
		private boolean stop = false;
		private RotatableRadarSystem radar;
		
		@Override
		public boolean run() {
			
			return !stop;
		}
		public void stop(){
			
		}
	}
	
	public static final double DEFAULT_MAX_ANGLE = 180;
	public static final double DEFAULT_MIN_ANGLE = -180;
	public static final double DEFAULT_ANGLE_CHANGE = 1.0;
	
	private RangeFinder rangeFinder;
	private Servo rotationBase;
	private double currentAngle;
	private double maxAngle;
	private double minAngle;
	private double maxRange;
	private double minRange;
	private double angleChange;
	private int scanDirection;
	private int scanCount;
	
	private Vector2[] objects;
	
	public RotatableRadarSystem(RangeFinder finder, Servo base, double angleChange, 
			double minAngle, double maxAngle){
		this.rangeFinder = finder;
		this.rotationBase = base;
		this.angleChange = angleChange;
		this.maxAngle = maxAngle;
		this.minAngle = minAngle;
		
		int scanCounts = (int) ((Math.abs(maxAngle) + Math.abs(minAngle)) / angleChange);
		objects = new Vector2[scanCounts];
	}
	
	private void setAngle(double angle){
		currentAngle = angle;
		rotationBase.setPosition(angle);
	}
	private void initScanning(){
		setAngle(minAngle);
		scanDirection = 1;
		scanCount = 0;
		
		int scanCounts = (int) ((Math.abs(maxAngle) + Math.abs(minAngle)) / angleChange);
		if(objects.length != scanCounts)
			objects = new Vector2[scanCounts];
	}
	private void scanIteration(){
		double newAngle = currentAngle + angleChange * scanDirection;
		if(newAngle >= maxAngle || newAngle <= minAngle){
			scanDirection *= -1;
			newAngle = currentAngle + angleChange * scanDirection;
		}
		setAngle(newAngle);
		rangeFinder.ping();
		FlashUtil.delay(50);
		double range = rangeFinder.getRangeCM();
		range = range < maxRange && range > minRange? range : -1;
		
		Vector2 res = Vector2.polar(range, newAngle);
		if(objects[scanCount] == null)
			objects[scanCount] = res;
		else 
			objects[scanCount].set(res);
		scanCount++;
	}
	
	public double getRange(double angle){
		int index = (int) ((angle - minAngle) / angleChange);
		if(index < 0 || index > objects.length)
			return -1;
		return objects[index].length();
	}
	public Vector2 getVector(int index){
		return objects[index];
	}
	public Vector2[] getVectors(){
		return Arrays.copyOf(objects, objects.length);
	}
}