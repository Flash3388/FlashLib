package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Vision;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.VoltageScalable;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.Rotatable;

public class VisionRotate extends Action implements VisionAction, VoltageScalable{
	
	private Rotatable driveTrain;
	private ModableMotor modable;
	private Vision vision;
	private boolean targetFound, centered, horizontal, scaleVoltage = false, lastDir;
	private double speed, lastSpeed, minSpeed, maxSpeed, baseSpeed;
	private byte margin, lastPixels, pixelDifference, differences;
	private int timeout, timeLost, centeredTimeout, timeCentered;
	
	public VisionRotate(Rotatable driveTrain, Vision vision, double speed, boolean horizontal, int margin, 
			int timeout, int centeredTime){
		this.vision = vision;
		this.driveTrain = driveTrain;
		this.baseSpeed = speed;
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
	public VisionRotate(Rotatable driveTrain, Vision vision, double speed, boolean horizontal){
		this(driveTrain, vision, speed, horizontal, ACCURACY_MARGIN, LOSS_TIMEOUT, ACTION_VALIDATION_TIMEOUT);
	}
	public VisionRotate(Rotatable driveTrain, Vision vision, double speed){
		this(driveTrain, vision, speed, true);
	}
	
	@Override
	protected void initialize() {
		targetFound = false;
		centered = false;
		timeCentered = -1;
		timeLost = -1;
		lastSpeed = 0;
		speed = baseSpeed;
		lastPixels = -1;
		pixelDifference = 0;
		differences = 0;
		
		if(minSpeed <= 0)
			minSpeed = DEFAULT_MIN_SPEED;
		if(maxSpeed > 1 || maxSpeed <= 0)
			maxSpeed = DEFAULT_MAX_SPEED;
		
		if(!vision.isRunning())
			vision.start();
		if(modable != null && modable.inBrakeMode())
			modable.enableBrakeMode(false);
	}
	@Override
	protected void execute() {
		boolean dir = true; 
		double rotateSpeed = 0;
		byte offset = 0;
		if(vision.hasNewAnalysis()){
			Analysis an = vision.getAnalysis();
			an.print();
			offset = (byte) (horizontal? an.horizontalDistance : -an.verticalDistance);
			targetFound = true;
			
			dir = offset > 0;
			offset = (byte) Math.abs(offset);
			
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
				rotateSpeed = speed * (offset / 100.0);
				rotateSpeed = Mathd.limit(rotateSpeed, minSpeed, maxSpeed);
			}
			
			if(lastPixels >= 0){
				pixelDifference = (byte) Math.abs(offset - lastPixels);
				differences++;
			}
			lastPixels = offset;
		}else{
			if(targetFound)
				timeLost = FlashUtil.millisInt();
			targetFound = false;
			rotateSpeed = lastSpeed > 0? minSpeed : 0;
			dir = lastDir;
		}
		if(lastDir != dir){
			speed /= 1.2;
			rotateSpeed /= 1.2;
			FlashUtil.getLog().log("Lowering Speed");
		}
		if(differences > 0 && Math.abs(pixelDifference - lastPixels) <= margin){
			rotateSpeed = 0;
			FlashUtil.getLog().log("Predicted Stop");
		}
		if(scaleVoltage && rotateSpeed != 0)
			rotateSpeed = FlashRoboUtil.scaleVoltageBus(rotateSpeed);
		FlashUtil.getLog().log("Speed: "+rotateSpeed+" Dir: "+dir+" Differences: "+differences+" PixelsD: "+pixelDifference + " L: "+lastPixels);
		driveTrain.rotate(rotateSpeed, dir);
		lastDir = dir;
		lastSpeed = rotateSpeed;
	}
	@Override
	protected boolean isFinished() {
		long millis = FlashUtil.millisInt();
		return (!targetFound && millis - timeLost >= timeout) || 
				(finiteCenteredTimeout() && isCentered() && millis - timeCentered >= centeredTimeout);
	}
	@Override
	protected void end() {
		driveTrain.stop();
		if (modable != null && modable.inBrakeMode())
			modable.enableBrakeMode(false);
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
		return baseSpeed;
	}
	@Override
	public void setSpeed(double sp){
		baseSpeed = sp;
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
	public int getCenterTimeout(){
		return centeredTimeout;
	}
	public void setCenterTimeout(int millis){
		centeredTimeout = millis;
	}
	public boolean isHorizontalRotate(){
		return horizontal;
	}
	public void setHorizontalRotate(boolean s){
		horizontal = s;
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