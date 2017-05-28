package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Vision;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.XAxisMovable;

public class VisionShift extends Action implements VisionAction{
	
	private XAxisMovable driveTrain;
	private ModableMotor modable;
	private Vision vision;
	private boolean targetFound, centered, horizontal, lastDir;
	private double speed, lastSpeed, minSpeed, maxSpeed;
	private byte margin;
	private int timeout, timeLost, centeredTimeout, timeCentered;
	
	public VisionShift(XAxisMovable driveTrain, Vision vision, double speed, boolean horizontal, int margin, 
			int timeout, int centeredTime){
		this.vision = vision;
		this.driveTrain = driveTrain;
		this.speed = speed;
		this.horizontal = horizontal;
		this.margin = (byte) margin;
		this.timeout = timeout;
		this.centeredTimeout = centeredTime;
		
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		if(driveTrain instanceof ModableMotor)
			modable = (ModableMotor)driveTrain;
	}
	public VisionShift(XAxisMovable driveTrain, Vision vision, double speed, boolean horizontal){
		this(driveTrain, vision, speed, horizontal, ACCURACY_MARGIN, LOSS_TIMEOUT, ACTION_VALIDATION_TIMEOUT);
	}
	public VisionShift(XAxisMovable driveTrain, Vision vision, double speed){
		this(driveTrain, vision, speed, true);
	}
	
	@Override
	protected void initialize() {
		targetFound = false;
		centered = false;
		timeCentered = -1;
		timeLost = -1;
		lastSpeed = 0;

		if(minSpeed <= 0)
			minSpeed = DEFAULT_MIN_SPEED;
		if(maxSpeed > 1 || maxSpeed <= 0)
			maxSpeed = DEFAULT_MAX_SPEED;
		
		if(!vision.isRunning())
			vision.start();
	}
	@Override
	protected void execute() {
		boolean dir = true;
		double rotateSpeed = 0;
		int offset = 0;
		if(vision.hasNewAnalysis()){
			Analysis an = vision.getAnalysis();
			offset = horizontal? an.horizontalDistance : -an.verticalDistance;
			targetFound = true;
			
			if(offset > -margin && offset < margin){//centered on target
				rotateSpeed = 0;
				if(modable != null && !modable.inBrakeMode())
					modable.enableBrakeMode(true);
				if(!centered){
					centered = true;
					timeCentered = FlashUtil.millisInt();
				}
			}else{//not centered on target
				centered = false;
				timeCentered = -1;
				dir = offset > 0;
				rotateSpeed = speed * (Math.abs(offset) / 100.0);
				rotateSpeed = Mathd.limit(rotateSpeed, minSpeed, maxSpeed);
			}
		}else{
			if(targetFound)
				timeLost = FlashUtil.millisInt();
			targetFound = false;
			rotateSpeed = lastSpeed > 0? minSpeed : 0;
			dir = lastDir;
		}
		driveTrain.driveX(rotateSpeed, dir);
		lastDir = dir;
		lastSpeed = rotateSpeed;
	}
	@Override
	protected boolean isFinished() {
		int millis = FlashUtil.millisInt();
		return (!targetFound && millis - timeLost >= timeout) || 
				(finiteCenteredTimeout() && isCentered() && millis - timeCentered >= centeredTimeout);
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
	
	public boolean finiteCenteredTimeout(){
		return centeredTimeout > 0;
	}
	public boolean isCentered(){
		return centered;
	}
	public int getPixelMargin(){
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
}
