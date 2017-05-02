package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Direction;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.Rotatable;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.VoltageScalable;

public class AngleRotateAction extends Action implements VoltageScalable{
	
	public static final double ANGLE_MARGIN = 10;
	public static final int MAX_MISSES = 5;
	
	private double currentAngle, toAngle, desiredAngle, angleMargin, angleConversion, angleAddition;
	private int direction, lastDir, misses, maxMisses;
	private double speed, minSpeed, maxSpeed;
	private boolean absolute, scaleVoltage = false;
	private Rotatable drive;
	private ModableMotor modable;
	private Gyro gyro;
	
	public AngleRotateAction(Rotatable drive, Gyro gyro, double speed, boolean absolute, double destAngle, int maxMisses){
		this.drive = drive;
		this.gyro = gyro;
		this.absolute = absolute;
		this.speed = speed;
		this.desiredAngle = destAngle;
		this.maxMisses = maxMisses;
		
		if(drive instanceof ModableMotor)
			modable = (ModableMotor)drive;
		System s = drive.getSystem();
		if(s != null)
			requires(s);
	}
	public AngleRotateAction(Rotatable drive, Gyro gyro, double speed, boolean absolute, double destAngle){
		this(drive, gyro, speed, absolute, destAngle, MAX_MISSES);
	}
	public AngleRotateAction(Rotatable drive, Gyro gyro, double speed, boolean absolute){
		this(drive, gyro, speed, absolute, 0);
	}
	public AngleRotateAction(Rotatable drive, Gyro gyro, double speed){
		this(drive, gyro, speed, false);
	}
	
	
	@Override
	protected void initialize(){
		if(absolute) {
			angleConversion = 0;
			angleAddition = 360;
		}
		else {
			angleConversion = Mathd.limitAngle(gyro.getAngle());
			angleAddition = 0;
		}
		
		if(maxSpeed <= minSpeed)
			maxSpeed = 1;
		
		calculatePositioning();
		lastDir = direction;
		misses = 0;
		FlashUtil.getLog().log("Dest angle: "+desiredAngle);
	}
	@Override
	protected void execute() {
		calculatePositioning();
		
		if(lastDir != direction){
			lastDir = direction;
			misses++;
			speed /= 2;
		}
		
		double angularDistance = direction == Direction.RIGHT ? toAngle : 360 - toAngle;
		double rotateSpeed = speed * (angularDistance / 100.0);
		FlashUtil.getLog().log("Speed: "+speed+" --- SpeedN: "+rotateSpeed+" \nDirection: "+direction+" CurrentAngle: "+currentAngle
				+" To angle: "+toAngle);
		rotateSpeed = Mathd.limit(rotateSpeed, minSpeed, maxSpeed);
		if(scaleVoltage)
			rotateSpeed = FlashRoboUtil.scaleVoltageBus(rotateSpeed);
		FlashUtil.getLog().log("After speed n:"+rotateSpeed+"\n");
		
		drive.rotate(rotateSpeed, direction);
	}
	@Override 
	protected boolean isFinished(){
		return (toAngle <= angleMargin && toAngle >= 360 - angleMargin) || misses >= maxMisses;
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
	private void calculatePositioning(){
		currentAngle = Mathd.limitAngle(gyro.getAngle() - angleConversion);
		toAngle = Mathd.limitAngle(desiredAngle - currentAngle + angleAddition);
		direction = (toAngle <= 180)? Direction.RIGHT : Direction.LEFT;
	}
	
	public void setSpeed(double speed){
		this.speed = speed;
	}
	public double getSpeed(){
		return speed;
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
	public void setMaxMisses(int misses){
		maxMisses = misses;
	}
	public int getMaxMisses(){
		return maxMisses;
	}
	public void setAngleMargin(double margin){
		angleMargin = margin;
	}
	public double getAngleMargin(){
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
