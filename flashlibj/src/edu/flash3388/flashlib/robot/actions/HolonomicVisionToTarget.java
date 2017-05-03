package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Vision;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;
import edu.flash3388.flashlib.robot.systems.ModableMotor;

public class HolonomicVisionToTarget extends Action implements VisionAction{
	
	private HolonomicDriveSystem driveTrain;
	private ModableMotor modable;
	private Vision vision;
	private boolean targetFound, centered, horizontalD, rotate, sideways = false;
	private int margin;
	private double speed, lastY, lastX, currentDistance, distanceThreshold, minSpeed, distanceMargin, maxSpeed;
	private long timeLost, timeout, centeredTimeout, passedTimeout, timepassed, timecentered;
	
	public HolonomicVisionToTarget(HolonomicDriveSystem driveTrain, Vision vision, double speed, 
			boolean rotate, boolean horizontalD,
			int margin, double distanceThreshold, long timeout, long passedTimeout, long centeredtimout){
		this.vision = vision;
		this.driveTrain = driveTrain;
		this.speed = speed;
		this.timeout = timeout;
		this.margin = margin;
		this.distanceMargin = margin * 1.5;
		this.distanceThreshold = distanceThreshold;
		this.centeredTimeout = centeredtimout;
		this.passedTimeout = passedTimeout;
		this.horizontalD = horizontalD;
		this.rotate = rotate;
		
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		modable = driveTrain;
	}
	public HolonomicVisionToTarget(HolonomicDriveSystem driveTrain, Vision vision, double speed, 
			boolean rotate, boolean horizontalD){
		this(driveTrain, vision, speed, rotate, horizontalD, ACCURACY_MARGIN, ACCURACY_MARGIN, LOSS_TIMEOUT, 
				ACTION_VALIDATION_TIMEOUT, ACTION_VALIDATION_TIMEOUT);
	}
	public HolonomicVisionToTarget(HolonomicDriveSystem driveTrain, Vision vision, double speed){
		this(driveTrain, vision, speed, false, false);
	}
	
	@Override
	protected void initialize() {
		targetFound = false;
		centered = false;
		currentDistance = -1;
		timecentered = -1;
		timepassed = -1;
		timeLost = -1;
		lastX = lastY = 0;
		
		if(minSpeed <= 0)
			minSpeed = DEFAULT_MIN_SPEED;
		if(maxSpeed > 1 || maxSpeed <= 0)
			maxSpeed = DEFAULT_MAX_SPEED;
		
		if(!vision.isRunning())
			vision.start();
	}
	@Override
	protected void execute() {
		double speedX = 0, speedY = 0;
		long millis = FlashUtil.millis();
		
		if(vision.hasNewAnalysis()){
			targetFound = true;
			timeLost = -1;
			
			Analysis an = vision.getAnalysis();
			an.print();
			int offset = an.horizontalDistance;
			currentDistance = horizontalD? calculateHorizontalDistance(an.targetDistance) :
												  an.targetDistance;
			
			if(offset > -margin && offset < margin){
				if(!centered){
					centered = true;
					timecentered = millis;
				}else if(millis - timecentered >= centeredTimeout/2){
					FlashUtil.getLog().log("Centered time");
					speedX = 0;
				}
			}else{
				centered = false;
				timecentered = -1;
				speedX = speed * (Math.abs(offset) / 100.0);
				speedX = Mathd.limit(speedX, minSpeed, maxSpeed);
				speedX *= offset < 0? 1 : -1;
			}
			if(inDistanceThreshold()){
				if(timepassed == -1)
					timepassed = millis;
				else if(millis - timepassed >= passedTimeout/2)
					speedY = 0;
			}else{
				double offsetD = distanceThreshold - currentDistance;
				speedY = speed * Math.abs(offsetD) / 100.0;
				speedX = Mathd.limit(speedY, minSpeed, maxSpeed);
				speedY *= offsetD < 0? 1 : -1;
			}
		}else{
			if(targetFound)
				timeLost = millis;
			targetFound = false;
			speedX = lastX > 0 ? minSpeed : 0;
			speedY = lastY > 0 ? minSpeed : 0;
		}
		if(centered && millis - timecentered >= centeredTimeout/2){
			FlashUtil.getLog().log("Centered time");
			speedX = 0;
		}
		FlashUtil.getLog().log("SpeedX: "+speedX+" SpeedY: "+speedY + " sideways: "+sideways+" rotate: "+rotate+
				" centered: "+centered);
		if(sideways) driveTrain.holonomicCartesian(-speedY * 2, speedX, 0);
		else if(!rotate) driveTrain.holonomicCartesian(speedX, speedY, 0);
		else driveTrain.holonomicCartesian(0, speedY, -speedX);
		lastX = speedX;
		lastY = speedY;
	}
	@Override
	protected boolean isFinished() {
		long millis = FlashUtil.millis();
		return (!targetFound && millis - timeLost >= timeout) || 
				((finiteCenteredTimeout() && isCentered() && millis - timecentered >= centeredTimeout) &&
						(finiteApproachTimeout() && inDistanceThreshold() && millis - timepassed >= passedTimeout));
	}
	@Override
	protected void end() {
		if(modable != null)
			modable.enableBrakeMode(true);
		driveTrain.stop();
		modable.enableBrakeMode(false);
	}
	
	@Override
	public void setMotorModeSource(ModableMotor modable){
		this.modable = modable;
	}
	
	private double calculateHorizontalDistance(double distance){
		double horizontal = vision.getTargetHeight();
		return Math.sqrt((distance * distance) - (horizontal * horizontal));
	}
	
	@Override
	public boolean targetInView(){
		return targetFound;
	}
	@Override
	public long getLossTimeout(){
		return timeout;
	}
	@Override
	public void setLossTimeout(long millis){
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
		return passedTimeout > 0;
	}
	public boolean finiteCenteredTimeout(){
		return centeredTimeout > 0;
	}
	public boolean validDistance(){
		return currentDistance > 0;
	}
	public boolean inDistanceThreshold(){
		return validDistance() && 
		(currentDistance >= distanceThreshold - distanceMargin && currentDistance <= distanceThreshold + distanceMargin);
	}
	public boolean isCentered(){
		return centered;
	}
	public double getDistanceMargin(){
		return distanceMargin;
	}
	public void setDistanceMargin(double margin){
		distanceMargin = margin;
	}
	public int getPixelMargin(){
		return margin;
	}
	public void setPixelMargin(int pixels){
		margin = pixels;
	}
	public long getCenterTimeout(){
		return centeredTimeout;
	}
	public void setCenterTimeout(long millis){
		centeredTimeout = millis;
	}
	public long getPastTimeout(){
		return passedTimeout;
	}
	public void setPastTimeout(long millis){
		passedTimeout = millis;
	}
	public double getDistanceThreshold(){
		return distanceThreshold;
	}
	public void setDistanceThreshold(double cm){
		distanceThreshold = cm;
	}
	public boolean isHorizontalDistance(){
		return horizontalD;
	}
	public void setHorizontalDistance(boolean en){
		horizontalD = en;
	}
	public boolean isSetToRotate(){
		return rotate;
	}
	public void setToRotate(boolean rotate){
		this.rotate = rotate;
	}
	public void setSideways(boolean side){
		sideways = side;
	}
	public boolean isSideways(){
		return sideways;
	}
}
