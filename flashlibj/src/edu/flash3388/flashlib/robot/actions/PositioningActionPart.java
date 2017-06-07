package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.SourceAction;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.FlashUtil;

public class PositioningActionPart extends SourceAction{

	private DoubleDataSource source;
	private double speed, distanceThreshold, distanceMargin, currentDistance;
	private int passedTimeout, timepassed;
	
	public PositioningActionPart(DoubleDataSource source, double speed, double distanceThreshold, 
			double distanceMargin, int passedTimeout){
		this.source = source;
		this.speed = speed;
		this.distanceMargin = distanceMargin;
		this.distanceThreshold = distanceThreshold;
		this.passedTimeout = passedTimeout;
	}
	public PositioningActionPart(DoubleDataSource source, double speed, double distanceThreshold){
		this(source, speed, distanceThreshold, 15.0, 100);
	}
	public PositioningActionPart(DoubleDataSource source, double speed){
		this(source, speed, 100.0);
	}
	
	@Override
	protected void initialize() {
		timepassed = -1;
		currentDistance = -1;
		dataSource.set(0);
	}
	@Override
	public void execute() {
		double speedY = 0;
		int millis = FlashUtil.millisInt();
		
		currentDistance = source.get();
		
		if(inDistanceThreshold()){
			if(timepassed == -1)
				timepassed = millis;
			else if(millis - timepassed >= passedTimeout / 2)
				speedY = 0;
		}else{
			double offsetD = distanceThreshold - currentDistance;
			speedY = speed * Math.abs(offsetD) / 100.0;
			speedY *= offsetD < 0? 1 : -1;
		}
		
		dataSource.set(speedY);
	}
	@Override
	protected boolean isFinished() {
		return finiteApproachTimeout() && inDistanceThreshold() && FlashUtil.millisInt() - timepassed >= passedTimeout;
	}
	@Override
	protected void end() {
	}
	
	public boolean finiteApproachTimeout(){
		return passedTimeout > 0;
	}
	public boolean validDistance(){
		return currentDistance > 0;
	}
	public boolean inDistanceThreshold(){
		return validDistance() && 
		(currentDistance >= distanceThreshold - distanceMargin && currentDistance <= distanceThreshold + distanceMargin);
	}
	
	public double getSpeed(){
		return speed;
	}
	public void setSpeed(double sp){
		speed = sp;
	}
	public double getDistanceMargin(){
		return distanceMargin;
	}
	public void setDistanceMargin(double margin){
		distanceMargin = margin;
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
}
