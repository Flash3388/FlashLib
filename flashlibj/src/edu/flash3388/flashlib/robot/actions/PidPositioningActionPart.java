package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.CombinedAction.ActionPart;
import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.util.FlashUtil;

public class PidPositioningActionPart extends ActionPart{

	private PidController pidcontroller;
	private double distanceMargin;
	private int passedTimeout, timepassed;
	
	public PidPositioningActionPart(PidSource source, double kp, double ki, double kd, double distanceThreshold, 
			double distanceMargin, int passedTimeout){
		this.distanceMargin = distanceMargin;
		this.passedTimeout = passedTimeout;
		
		pidcontroller = new PidController(kp, ki, kd);
		pidcontroller.setPIDSource(source);
		pidcontroller.setInputLimit(1000);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PidPositioningActionPart(PidSource source, double kp, double ki, double kd, double distanceThreshold){
		this(source, kp, ki, kd, distanceThreshold, 15.0, 100);
	}
	public PidPositioningActionPart(PidSource source, double kp, double ki, double kd){
		this(source, kp, ki, kd, 100.0);
	}
	
	@Override
	protected void initialize() {
		timepassed = -1;
	}
	@Override
	public double getExecute() {
		if(inDistanceThreshold()){
			int millis = FlashUtil.millisInt();
			if(timepassed == -1)
				timepassed = millis;
			else if(millis - timepassed >= passedTimeout / 2)
				return 0;
		}
		return pidcontroller.calculate();
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
	public boolean inDistanceThreshold(){
		double current = pidcontroller.getSource().pidGet();
		return current > 0 && 
		(current >= getDistanceThreshold() - distanceMargin && current <= getDistanceThreshold() + distanceMargin);
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
		return pidcontroller.getSetPoint();
	}
	public void setDistanceThreshold(double cm){
		pidcontroller.setSetPoint(cm);
	}
}
