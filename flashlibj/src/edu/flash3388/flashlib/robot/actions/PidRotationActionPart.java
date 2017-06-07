package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.CombinedAction.ActionPart;
import edu.flash3388.flashlib.util.FlashUtil;

public class PidRotationActionPart extends ActionPart{

	private PidController pidcontroller;
	private double rotationMargin;
	private int passedTimeout, timepassed;
	
	public PidRotationActionPart(PidSource source, double kp, double ki, double kd, double rotationThreshold, 
			double rotationMargin, int passedTimeout){
		this.rotationMargin = rotationMargin;
		this.passedTimeout = passedTimeout;
		
		pidcontroller = new PidController(kp, ki, kd);
		pidcontroller.setPIDSource(source);
		pidcontroller.setInputLimit(1000);
		pidcontroller.setSetPoint(rotationThreshold);
	}
	public PidRotationActionPart(PidSource source, double kp, double ki, double kd, double rotationThreshold){
		this(source, kp, ki, kd, rotationThreshold, 15.0, 100);
	}
	public PidRotationActionPart(PidSource source, double kp, double ki, double kd){
		this(source, kp, ki, kd, 100.0);
	}
	
	@Override
	protected void initialize() {
		timepassed = -1;
	}
	@Override
	public double getExecute() {
		if(inRotationThreshold()){
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
		return finiteApproachTimeout() && inRotationThreshold() && FlashUtil.millisInt() - timepassed >= passedTimeout;
	}
	@Override
	protected void end() {
	}
	
	public boolean finiteApproachTimeout(){
		return passedTimeout > 0;
	}
	public boolean inRotationThreshold(){
		double current = pidcontroller.getSource().pidGet();
		return current > 0 && 
		(current >= getRotationThreshold() - rotationMargin && current <= getRotationThreshold() + rotationMargin);
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
		return pidcontroller.getSetPoint();
	}
	public void setRotationThreshold(double cm){
		pidcontroller.setSetPoint(cm);
	}
}