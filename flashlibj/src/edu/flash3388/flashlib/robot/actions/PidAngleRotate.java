package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.VoltageScalable;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.Rotatable;

public class PidAngleRotate extends Action implements VoltageScalable{
	
	private double desiredAngle;
	private byte angleMargin;
	private double minSpeed, maxSpeed;
	private boolean absolute, scaleVoltage = false;
	private Rotatable drive;
	private ModableMotor modable;
	private PidController pidcontroller;
	
	public PidAngleRotate(Rotatable drive, Gyro gyro, double kp, double ki, double kd, boolean absolute, 
			double destAngle){
		this.drive = drive;
		this.absolute = absolute;
		this.desiredAngle = destAngle;
		
		pidcontroller = new PidController(kp, ki, kd);
		pidcontroller.setPIDSource(new PidController.GyroPidSource(gyro));
		pidcontroller.setInputLimit(360);
		
		if(drive instanceof ModableMotor)
			modable = (ModableMotor)drive;
		System s = drive.getSystem();
		if(s != null)
			requires(s);
	}
	public PidAngleRotate(Rotatable drive, Gyro gyro, double kp, double ki, double kd, boolean absolute){
		this(drive, gyro, kp, ki, kd, absolute, 0);
	}
	public PidAngleRotate(Rotatable drive, Gyro gyro, double kp, double ki, double kd){
		this(drive, gyro, kp, ki, kd, false);
	}
	
	
	@Override
	protected void initialize(){
		if(maxSpeed <= minSpeed)
			maxSpeed = 1;
		
		pidcontroller.setSetPoint(calculatePositioning());
	}
	@Override
	protected void execute() {
		double rotateSpeed = pidcontroller.calculate();
		boolean dir = rotateSpeed > 0;
		rotateSpeed = Math.abs(rotateSpeed);
		rotateSpeed = Mathd.limit(rotateSpeed, minSpeed, maxSpeed);
		if(scaleVoltage)
			rotateSpeed = FlashRoboUtil.scaleVoltageBus(rotateSpeed);
		
		drive.rotate(rotateSpeed, dir);
	}
	@Override 
	protected boolean isFinished(){
		return (getAngle() <= desiredAngle + angleMargin && getAngle() >= desiredAngle - angleMargin);
	}
	@Override
	protected void end() {
		boolean changed = false;
		if(modable != null && !modable.inBrakeMode()){
			modable.enableBrakeMode(true);
			changed = true;
		}
		drive.stop();
		if(modable != null && changed)
			modable.enableBrakeMode(false);
	}
	private double calculatePositioning(){
		double currentAngle = absolute? Mathd.limitAngle(getAngle()) : 0;
		return Mathd.limitAngle(desiredAngle - currentAngle + (absolute? 360 : 0));
	}
	private double getAngle(){
		return pidcontroller.getSource().pidGet();
	}
	
	public void setMinSpeed(double speed){
		this.minSpeed = speed;
	}
	public double getMinSpeed(){
		return minSpeed;
	}
	public void setMaxSpeed(double speed){
		this.maxSpeed = speed;
	}
	public double getMaxSpeed(){
		return maxSpeed;
	}
	
	public void setDestAngle(double angle){
		this.desiredAngle = angle;
	}
	public double getDestAngle(){
		return desiredAngle;
	}
	public void setAngleMargin(int margin){
		angleMargin = (byte) margin;
	}
	public int getAngleMargin(){
		return angleMargin;
	}
	public void setRotationMode(boolean absolute){
		if(isRunning())
			throw new IllegalStateException("Cannot change mode while running");
		this.absolute = absolute;
	}
	public boolean isRotationAbsolute(){
		return absolute;
	}
	
	public void setMotorSourceMode(ModableMotor modable){
		this.modable = modable;
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
