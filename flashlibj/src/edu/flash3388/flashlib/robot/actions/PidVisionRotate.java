package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.PidType;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.VoltageScalable;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.Rotatable;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.Vision;

public class PidVisionRotate extends Action implements VisionAction{

	private static class DoublePidSource implements PidSource{
		private double d;
		
		public void set(double d){
			this.d = d;
		}
		@Override
		public double pidGet() {
			return d;
		}
		@Override
		public PidType getType() {
			return PidType.Displacement;
		}
	}
	
	private Rotatable driveTrain;
	private ModableMotor modable;
	private Vision vision;
	private DoublePidSource source;
	private PidController pidcontroller;
	private boolean horizontal, targetFound, centered, lastDir;
	private byte margin = 0;
	private double minSpeed, maxSpeed, lastSpeed;
	private int timeout, timeLost, centeredTimeout, timeCentered;
	
	public PidVisionRotate(Rotatable driveTrain, Vision vision, double kp, double ki, double kd, 
			boolean horizontal, int timeout, int centeredTime){
		this.vision = vision;
		this.driveTrain = driveTrain;
		this.timeout = timeout;
		this.centeredTimeout = centeredTime;
		
		pidcontroller = new PidController(kp, ki, kd, 0);
		source = new DoublePidSource();
		pidcontroller.setPIDSource(source);
		pidcontroller.setInputLimit(360);
		
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		if(driveTrain instanceof ModableMotor)
			modable = (ModableMotor)driveTrain;
	}
	public PidVisionRotate(Rotatable driveTrain, Vision vision, double kp, double ki, double kd, boolean horizontal){
		this(driveTrain, vision, kp, ki, kd, horizontal, LOSS_TIMEOUT, ACTION_VALIDATION_TIMEOUT);
	}
	public PidVisionRotate(Rotatable driveTrain, Vision vision, double kp, double ki, double kd){
		this(driveTrain, vision, kp, ki, kd, true, LOSS_TIMEOUT, ACTION_VALIDATION_TIMEOUT);
	}
	
	@Override
	protected void initialize() {
		targetFound = false;
		centered = false;
		timeCentered = -1;
		timeLost = -1;
		lastDir = true;
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
		boolean dir = false; 
		double rotateSpeed = 0;
		int offset = 0;
		if(vision.hasNewAnalysis()){
			Analysis an = vision.getAnalysis();
			an.print();
			offset = horizontal? an.horizontalDistance : -an.verticalDistance;
			targetFound = true;
			
			dir = offset > 0;
			offset = Math.abs(offset);
			
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
				source.set(offset);
				rotateSpeed = pidcontroller.calculate();
				rotateSpeed = Mathd.limit(rotateSpeed, minSpeed, maxSpeed);
			}
		}else{
			if(targetFound)
				timeLost = FlashUtil.millisInt();
			targetFound = false;
			rotateSpeed = lastSpeed > 0? minSpeed : 0;
			dir = lastDir;
		}
		
		driveTrain.rotate(rotateSpeed, dir);
	}
	@Override
	protected boolean isFinished() {
		long millis = FlashUtil.millis();
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
	public double getSpeed(){return 0;}
	@Override
	public void setSpeed(double sp){}
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
}
