package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Direction;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.Rotatable;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.VoltageScalable;

public class AngleRotate extends Action implements VoltageScalable{
	
	public static final byte ANGLE_MARGIN = 10;
	public static final byte MAX_MISSES = 5;
	
	private double currentAngle, toAngle, desiredAngle, angleConversion, angleAddition;
	private byte dir, lastDir, misses, maxMisses, angleMargin;
	private double speed, minSpeed, maxSpeed;
	private boolean absolute, scaleVoltage = false;
	private Rotatable drive;
	private ModableMotor modable;
	private Gyro gyro;
	
	public AngleRotate(Rotatable drive, Gyro gyro, double speed, boolean absolute, double destAngle, int maxMisses){
		this.drive = drive;
		this.gyro = gyro;
		this.absolute = absolute;
		this.speed = speed;
		this.desiredAngle = destAngle;
		this.maxMisses = (byte) maxMisses;
		
		if(drive instanceof ModableMotor)
			modable = (ModableMotor)drive;
		System s = drive.getSystem();
		if(s != null)
			requires(s);
	}
	public AngleRotate(Rotatable drive, Gyro gyro, double speed, boolean absolute, double destAngle){
		this(drive, gyro, speed, absolute, destAngle, MAX_MISSES);
	}
	public AngleRotate(Rotatable drive, Gyro gyro, double speed, boolean absolute){
		this(drive, gyro, speed, absolute, 0);
	}
	public AngleRotate(Rotatable drive, Gyro gyro, double speed){
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
		lastDir = dir;
		misses = 0;
	}
	@Override
	protected void execute() {
		calculatePositioning();
		
		if(lastDir != dir){
			lastDir = dir;
			misses++;
			speed /= 2;
		}
		
		double angularDistance = dir == Direction.RIGHT ? toAngle : 360 - toAngle;
		double rotateSpeed = speed * (angularDistance / 100.0);
		rotateSpeed = Mathd.limit(rotateSpeed, minSpeed, maxSpeed);
		if(scaleVoltage)
			rotateSpeed = FlashRoboUtil.scaleVoltageBus(rotateSpeed);
		
		drive.rotate(rotateSpeed, dir);
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
		dir = (toAngle <= 180)? Direction.RIGHT : Direction.LEFT;
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
		maxMisses = (byte) misses;
	}
	public int getMaxMisses(){
		return maxMisses;
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
