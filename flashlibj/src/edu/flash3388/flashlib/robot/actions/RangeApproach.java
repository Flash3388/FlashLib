package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.VoltageScalable;
import edu.flash3388.flashlib.robot.devices.RangeFinder;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.YAxisMovable;
import edu.flash3388.flashlib.util.FlashUtil;

public class RangeApproach extends Action implements VoltageScalable{
	
	private YAxisMovable driveTrain;
	private ModableMotor modable;
	private RangeFinder finder;
	private double speed, distanceThreshold, currentDistance, distanceMargin, minSpeed = -1, maxSpeed = 1;
	private long pastTimeout, timePassed;
	private boolean scaleVoltage = false;
	
	public RangeApproach(YAxisMovable driveTrain, RangeFinder finder, double speed,
			double distanceThreshold, double margin, long pastTimeout){
		this.driveTrain = driveTrain;
		this.speed = speed;
		this.distanceThreshold = distanceThreshold;
		this.pastTimeout = pastTimeout;
		this.distanceMargin = margin;
		this.finder = finder;
		
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		if(driveTrain instanceof ModableMotor)
			modable = (ModableMotor)driveTrain;
	}
	public RangeApproach(YAxisMovable driveTrain, RangeFinder finder, double speed){
		this(driveTrain, finder, speed, VisionAction.ACCURACY_MARGIN * 2, VisionAction.ACCURACY_MARGIN, 
				VisionAction.ACTION_VALIDATION_TIMEOUT);
	}
	
	@Override
	protected void initialize() {
		currentDistance = -1;
		timePassed = -1;
		
		if(minSpeed <= 0)
			minSpeed = VisionAction.DEFAULT_MIN_SPEED;
		if(maxSpeed > 1 || maxSpeed <= 0)
			maxSpeed = VisionAction.DEFAULT_MAX_SPEED;
		
		if(modable != null)
			modable.enableBrakeMode(true);
	}
	@Override
	protected void execute() {
		double moveSpeed = 0;
		int dir = 0;
		currentDistance = finder.getRangeCM();
		if(inDistanceThreshold()){
			if(timePassed == -1)
				timePassed = FlashUtil.millis();
			moveSpeed = 0;
		}else{
			double offset = distanceThreshold - currentDistance;
			dir = offset < 0? 1 : -1;
			moveSpeed = speed * Math.abs(offset) / 100.0;
			moveSpeed = Mathd.limit(moveSpeed, minSpeed, maxSpeed);
		}
		if(isVoltageScaling() && moveSpeed != 0)
			moveSpeed = FlashRoboUtil.scaleVoltageBus(moveSpeed);
		FlashUtil.getLog().log("Speed: "+moveSpeed+" Dir: "+dir + " Dist: "+currentDistance + " Thres: "+distanceThreshold);
		driveTrain.driveY(moveSpeed, dir);
	}
	@Override
	protected boolean isFinished() {
		long millis = FlashUtil.millis();
		return (finiteApproachTimeout() && inDistanceThreshold() && millis - timePassed >= pastTimeout);
	}
	@Override
	protected void end() {
		driveTrain.stop();
		if(modable != null)
			modable.enableBrakeMode(false);
	}
	
	public void setMotorModeSource(ModableMotor modable){
		this.modable = modable;
	}
	
	public RangeFinder getRangeFinder(){
		return finder;
	}
	public void setRangeFinder(RangeFinder finder){
		this.finder = finder;
	}
	public double getSpeed(){
		return speed;
	}
	public void setSpeed(double sp){
		speed = sp;
	}
	public double getMinSpeed(){
		return minSpeed;
	}
	public void setMinSpeed(double min){
		minSpeed = min;
	}
	public double getMaxSpeed(){
		return maxSpeed;
	}
	public void setMaxSpeed(double max){
		maxSpeed = max;
	}
	
	public boolean finiteApproachTimeout(){
		return pastTimeout > 0;
	}
	public boolean inDistanceThreshold(){
		return currentDistance > 0 && 
		(currentDistance >= distanceThreshold - distanceMargin && currentDistance <= distanceThreshold + distanceMargin);
	}
	public long getPastTimeout(){
		return pastTimeout;
	}
	public void setPastTimeout(long millis){
		pastTimeout = millis;
	}
	public double getDistanceThreshold(){
		return distanceThreshold;
	}
	public void setDistanceThreshold(double cm){
		distanceThreshold = cm;
	}
	public double getDistanceMargin(){
		return distanceMargin;
	}
	public void setDistanceMargin(double cm){
		distanceMargin = cm;
	}
	@Override
	public void enableVoltageScaling(boolean en) {
		scaleVoltage = en;
	}
	@Override
	public boolean isVoltageScaling() {
		return scaleVoltage;
	}
}
