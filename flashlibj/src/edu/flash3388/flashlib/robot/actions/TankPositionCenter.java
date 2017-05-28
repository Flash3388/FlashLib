package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;
import edu.flash3388.flashlib.util.FlashUtil;

public class TankPositionCenter extends Action{
	
	private TankDriveSystem driveTrain;
	private ModableMotor modable;
	private PidController pidcontroller;
	private PidSource rotateSource, distanceSource;
	private boolean centered, posCentered;
	private double distanceThreshold, minSpeed, distanceMargin = 10, maxSpeed, rotateThreshold, rotateMargin = 10;
	private int centeredTimeout, passedTimeout, timepassed, timecentered;
	
	public TankPositionCenter(TankDriveSystem driveTrain, double kp, double ki, double kd,
			double rotateThreshold, double distanceThreshold, int passedTimeout, int centeredtimout){
		this.driveTrain = driveTrain;
		this.distanceThreshold = distanceThreshold;
		this.centeredTimeout = centeredtimout;
		this.passedTimeout = passedTimeout;
		this.rotateThreshold = rotateThreshold;
		
		pidcontroller = new PidController(kp, ki, kd);
		
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		modable = driveTrain;
	}
	public TankPositionCenter(TankDriveSystem driveTrain, double kp, double ki, double kd){
		this(driveTrain, kp, ki, kd, VisionAction.ACCURACY_MARGIN, VisionAction.ACCURACY_MARGIN,
				VisionAction.ACTION_VALIDATION_TIMEOUT, VisionAction.ACTION_VALIDATION_TIMEOUT);
	}
	
	@Override
	protected void initialize() {		
		if(minSpeed <= 0)
			minSpeed = VisionAction.DEFAULT_MIN_SPEED;
		if(maxSpeed > 1 || maxSpeed <= 0)
			maxSpeed = VisionAction.DEFAULT_MAX_SPEED;
		
		if(distanceSource == null && rotateSource == null)
			throw new IllegalArgumentException("No valid pid sources available");
		
		centered = false;
		posCentered = false;
		timecentered = -1;
		timepassed = -1;
		
		enableBrakeMode(false);
	}
	@Override
	protected void execute() {
		double speedX = 0, speedY = 0;
		int millis = FlashUtil.millisInt();
		
		if(distanceSource != null){
			pidcontroller.setPIDSource(distanceSource);
			pidcontroller.setSetPoint(distanceThreshold);
			speedY = pidcontroller.calculate();
			
			double currentDistance = distanceSource.pidGet();
			if(currentDistance >= distanceThreshold - distanceMargin && 
					currentDistance <= distanceThreshold + distanceMargin){
				if(!posCentered){
					posCentered = true;
					timepassed = millis;
				}else if(millis - timepassed >= passedTimeout/2){
					speedY = 0;
					enableBrakeMode(true);
				}
			}
		}
		if(rotateSource != null){
			pidcontroller.setPIDSource(rotateSource);
			pidcontroller.setSetPoint(rotateThreshold);
			speedX = pidcontroller.calculate();
			
			double currentRotate = rotateSource.pidGet();
			if(currentRotate >= rotateThreshold - rotateMargin && 
					currentRotate <= rotateThreshold + rotateMargin){
				if(!centered){
					centered = true;
					timecentered = millis;
				}else if(millis - timecentered >= centeredTimeout/2){
					speedX = 0;
					enableBrakeMode(true);
				}
			}
		}
		
		speedX = Mathd.limit(speedX, minSpeed, maxSpeed);
		speedY = Mathd.limit(speedY, minSpeed, maxSpeed);
		
		driveTrain.arcadeDrive(speedY, speedX);
	}
	@Override
	protected boolean isFinished() {
		int millis = FlashUtil.millisInt();
		return ((missingRotateSource() || (finiteCenteredTimeout() && isCentered() && millis - timecentered >= centeredTimeout)) &&
			(missingDistanceSource() || (finiteApproachTimeout() && inDistanceThreshold() && millis - timepassed >= passedTimeout)));
	}
	@Override
	protected void end() {
		driveTrain.stop();
		enableBrakeMode(false);
	}
	
	private void enableBrakeMode(boolean enable){
		if(modable != null)
			modable.enableBrakeMode(enable);
	}
	
	public void setMotorModeSource(ModableMotor modable){
		this.modable = modable;
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
		minSpeed = max;
	}
	
	public void setRotateSource(PidSource source){
		rotateSource = source;
	}
	public void setDistanceSource(PidSource source){
		distanceSource = source;
	}
	
	public boolean missingRotateSource(){
		return rotateSource == null;
	}
	public boolean missingDistanceSource(){
		return distanceSource == null;
	}
	public boolean finiteApproachTimeout(){
		return passedTimeout > 0;
	}
	public boolean finiteCenteredTimeout(){
		return centeredTimeout > 0;
	}
	public boolean inDistanceThreshold(){
		return posCentered;
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
	public double getRotateMargin(){
		return rotateMargin;
	}
	public void setRotateMargin(double margin){
		rotateMargin = margin;
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
	public double getRotateThreshold(){
		return rotateThreshold;
	}
	public void setRotateThreshold(double th){
		rotateThreshold = th;
	}
	public double getDistanceThreshold(){
		return distanceThreshold;
	}
	public void setDistanceThreshold(double cm){
		distanceThreshold = cm;
	}
}
