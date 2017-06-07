package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Vision;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.YAxisMovable;

public class VisionApproach extends Action implements VisionAction{
	
	private YAxisMovable driveTrain;
	private ModableMotor modable;
	private Vision vision;
	private boolean targetFound;
	private double speed, lastSpeed, distanceThreshold, currentDistance, distanceMargin, minSpeed = -1, maxSpeed = 1;
	private int timeout, timeLost, pastTimeout, timePassed;
	private boolean lastDir;
	
	public VisionApproach(YAxisMovable driveTrain, Vision vision, double speed, 
			double distanceThreshold, double margin,  int timeout, int pastTimeout){
		this.vision = vision;
		this.driveTrain = driveTrain;
		this.speed = speed;
		this.distanceThreshold = distanceThreshold;
		this.timeout = timeout;
		this.pastTimeout = pastTimeout;
		this.distanceMargin = margin;
		
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		if(driveTrain instanceof ModableMotor)
			modable = (ModableMotor)driveTrain;
	}
	public VisionApproach(YAxisMovable driveTrain, Vision vision, double speed){
		this(driveTrain, vision, speed, ACCURACY_MARGIN * 2, ACCURACY_MARGIN, LOSS_TIMEOUT, ACTION_VALIDATION_TIMEOUT);
	}
	
	@Override
	protected void initialize() {
		targetFound = false;
		currentDistance = -1;
		timeLost = -1;
		lastSpeed = 0;
		timePassed = -1;
		lastDir = true;
		
		if(minSpeed <= 0)
			minSpeed = DEFAULT_MIN_SPEED;
		if(maxSpeed > 1 || maxSpeed <= 0)
			maxSpeed = DEFAULT_MAX_SPEED;
		
		if(!vision.isRunning())
			vision.start();
	}
	@Override
	protected void execute() {
		double moveSpeed = 0;
		boolean dir = true;
		if(vision.hasNewAnalysis()){
			Analysis an = vision.getAnalysis();
			currentDistance = an.targetDistance;
			targetFound = true;
			if(inDistanceThreshold()){
				if(timePassed == -1)
					timePassed = FlashUtil.millisInt();
				else if(FlashUtil.millisInt() - timePassed >= pastTimeout/2)
					moveSpeed = 0;
			}else{
				double offset = distanceThreshold - currentDistance;
				moveSpeed = speed;
				dir = offset < 0;
				moveSpeed = Mathd.limit(moveSpeed, minSpeed, maxSpeed);
			}
		}else{
			if(targetFound)
				timeLost = FlashUtil.millisInt();
			targetFound = false;
			moveSpeed = lastSpeed > 0? minSpeed : 0;
			dir = lastDir;
		}
		driveTrain.driveY(moveSpeed, dir);
		lastSpeed = moveSpeed;
		lastDir = dir;
	}
	@Override
	protected boolean isFinished() {
		int millis = FlashUtil.millisInt();
		return (!targetFound && millis - timeLost >= timeout) || 
				(finiteApproachTimeout() && inDistanceThreshold() && millis - timePassed >= pastTimeout);
	}
	@Override
	protected void end() {
		driveTrain.stop();
	}
	
	@Override
	public void setMotorModeSource(ModableMotor modable){
		this.modable = modable;
	}
	
	@Override
	public boolean targetInView(){
		return targetFound;
	}
	@Override
	public int getLossTimeout(){
		return timeout;
	}
	@Override
	public void setLossTimeout(int millis){
		timeout = millis;
	}
	@Override
	public Vision getVision(){
		return vision;
	}
	@Override
	public void setVision(Vision vision){
		this.vision = vision;
	}
	@Override
	public double getSpeed(){
		return speed;
	}
	@Override
	public void setSpeed(double sp){
		speed = sp;
	}
	@Override
	public double getMinSpeed(){
		return minSpeed;
	}
	@Override
	public void setMinSpeed(double min){
		minSpeed = min;
	}
	@Override
	public double getMaxSpeed(){
		return maxSpeed;
	}
	@Override
	public void setMaxSpeed(double max){
		minSpeed = max;
	}
	
	
	
	public boolean finiteApproachTimeout(){
		return pastTimeout > 0;
	}
	public boolean inDistanceThreshold(){
		return currentDistance > 0 && 
		(currentDistance >= distanceThreshold - distanceMargin && currentDistance <= distanceThreshold + distanceMargin);
	}
	public int getPastTimeout(){
		return pastTimeout;
	}
	public void setPastTimeout(int millis){
		pastTimeout = millis;
	}
	public double getDistanceThreshold(){
		return distanceThreshold;
	}
	public void setDistanceThreshold(double cm){
		distanceThreshold = cm;
	}
}
