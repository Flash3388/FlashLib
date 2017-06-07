package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Vision;

public class TankVision extends Action implements VisionAction{
	
	private TankDriveSystem driveTrain;
	private ModableMotor modable;
	private Vision vision;
	private boolean targetFound, centered, horizontalD;
	private byte margin;
	private double speed, lastY, lastX, currentDistance, distanceThreshold, minSpeed, distanceMargin, maxSpeed;
	private int timeLost, timeout, centeredTimeout, passedTimeout, timepassed, timecentered;
	
	public TankVision(TankDriveSystem driveTrain, Vision vision, double speed, 
			boolean horizontalD,
			byte margin, double distanceThreshold, int timeout, int passedTimeout, int centeredtimout){
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
		
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		modable = driveTrain;
	}
	public TankVision(TankDriveSystem driveTrain, Vision vision, double speed, 
			boolean rotate, boolean horizontalD){
		this(driveTrain, vision, speed, horizontalD, ACCURACY_MARGIN, ACCURACY_MARGIN, LOSS_TIMEOUT, 
				ACTION_VALIDATION_TIMEOUT, ACTION_VALIDATION_TIMEOUT);
	}
	public TankVision(TankDriveSystem driveTrain, Vision vision, double speed){
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
		int millis = FlashUtil.millisInt();
		
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
		
		driveTrain.arcadeDrive(speedY, speedX);
		
		lastX = speedX;
		lastY = speedY;
	}
	@Override
	protected boolean isFinished() {
		int millis = FlashUtil.millisInt();
		return (!targetFound && millis - timeLost >= timeout) || 
				((finiteCenteredTimeout() && isCentered() && millis - timecentered >= centeredTimeout) &&
						(finiteApproachTimeout() && inDistanceThreshold() && millis - timepassed >= passedTimeout));
	}
	@Override
	protected void end() {
		driveTrain.stop();
	}
	
	@Override
	public void setMotorModeSource(ModableMotor modable){
		this.modable = modable;
	}
	
	private double calculateHorizontalDistance(double distance){
		//double horizontal = vision.getTargetHeight();
		return Math.sqrt((distance * distance) - (0.0 * 0.0));
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
	public byte getPixelMargin(){
		return margin;
	}
	public void setPixelMargin(int pixels){
		margin = (byte) pixels;
	}
	public long getCenterTimeout(){
		return centeredTimeout;
	}
	public void setCenterTimeout(int millis){
		centeredTimeout = millis;
	}
	public int getPastTimeout(){
		return passedTimeout;
	}
	public void setPastTimeout(int millis){
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
}
