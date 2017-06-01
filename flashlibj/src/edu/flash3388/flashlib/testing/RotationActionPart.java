package edu.flash3388.flashlib.testing;

import edu.flash3388.flashlib.robot.CombinedAction.ActionPart;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.FlashUtil;

public class RotationActionPart extends ActionPart{

	private DoubleDataSource source;
	private double speed, rotationThreshold, rotationMargin, currentRotation;
	private int passedTimeout, timepassed;
	
	public RotationActionPart(DoubleDataSource source, double speed, double rotationThreshold, 
			double rotationMargin, int passedTimeout){
		this.source = source;
		this.speed = speed;
		this.rotationMargin = rotationMargin;
		this.rotationThreshold = rotationThreshold;
		this.passedTimeout = passedTimeout;
	}
	public RotationActionPart(DoubleDataSource source, double speed, double rotationThreshold){
		this(source, speed, rotationThreshold, 15.0, 100);
	}
	public RotationActionPart(DoubleDataSource source, double speed){
		this(source, speed, 100.0);
	}
	
	@Override
	protected void initialize() {
		timepassed = -1;
		currentRotation = -1;
	}
	@Override
	public double getExecute() {
		double speedY = 0;
		int millis = FlashUtil.millisInt();
		
		currentRotation = source.get();
		
		if(inRotationThreshold()){
			if(timepassed == -1)
				timepassed = millis;
			else if(millis - timepassed >= passedTimeout / 2)
				speedY = 0;
		}else{
			double offsetD = rotationThreshold - currentRotation;
			speedY = speed * Math.abs(offsetD) / 100.0;
			speedY *= offsetD < 0? 1 : -1;
		}
		
		return speedY;
	}
	@Override
	protected boolean isFinished() {
		return finiteApproachTimeout() && inRotationThreshold() && FlashUtil.millisInt() - timepassed >= passedTimeout;
	}
	@Override
	protected void end() {
	}
	
	public boolean finiteApproachTimeout(){
		return passedTimeout > 0;
	}
	public boolean validRotationData(){
		return currentRotation > 0;
	}
	public boolean inRotationThreshold(){
		return validRotationData() && 
		(currentRotation >= rotationThreshold - rotationMargin && currentRotation <= rotationThreshold + rotationMargin);
	}
	
	public double getSpeed(){
		return speed;
	}
	public void setSpeed(double sp){
		speed = sp;
	}
	public double getRotationMargin(){
		return rotationMargin;
	}
	public void setRotationMargin(double margin){
		rotationMargin = margin;
	}
	public int getPastTimeout(){
		return passedTimeout;
	}
	public void setPastTimeout(int millis){
		passedTimeout = millis;
	}
	public double getRotationThreshold(){
		return rotationThreshold;
	}
	public void setRotationThreshold(double cm){
		rotationThreshold = cm;
	}
}
