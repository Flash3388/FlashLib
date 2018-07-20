package edu.flash3388.flashlib.robot.scheduling.actions.combined;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.control.PIDController;
import edu.flash3388.flashlib.robot.control.PIDSource;
import edu.flash3388.flashlib.robot.scheduling.PropertyAction;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class PIDDistanceActionPart extends PropertyAction implements PIDAction{

	private PIDController pidcontroller;
	private double distanceMargin;
	
	private int timeInThreshold = 0, thresholdStart = 0;
	
	public PIDDistanceActionPart(PIDController controller, double distanceMargin){
		this.distanceMargin = distanceMargin;
		this.pidcontroller = controller;
	}
	public PIDDistanceActionPart(PIDController controller){
		this(controller, 15.0);
	}
	public PIDDistanceActionPart(PIDSource source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource distanceThreshold, 
			double distanceMargin){
		this.distanceMargin = distanceMargin;
		
		pidcontroller = new PIDController(kp, ki, kd, kf);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PIDDistanceActionPart(PIDSource source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	public PIDDistanceActionPart(PIDSource source, double kp, double ki, double kd, double kf, DoubleSource distanceThreshold, 
			double distanceMargin){
		this.distanceMargin = distanceMargin;
		
		pidcontroller = new PIDController(kp, ki, kd, kf);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PIDDistanceActionPart(PIDSource source, double kp, double ki, double kd, double kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	
	@Override
	protected void initialize() {
		set(0);
		pidcontroller.setEnabled(true);
		pidcontroller.reset();
		thresholdStart = 0;
	}
	@Override
	public void execute() {
		if(!pidcontroller.isEnabled() || inDistanceThreshold()){
			set(0);
			if(thresholdStart < 1)
				thresholdStart = FlashUtil.millisInt();
		}
		else {
			if(thresholdStart >= 1)
				thresholdStart = 0;
			set(pidcontroller.calculate());
		}
	}
	@Override
	protected boolean isFinished() {
		return inDistanceThreshold() && FlashUtil.millisInt() - thresholdStart >= timeInThreshold;
	}
	@Override
	protected void end() {
		set(0);
	}
	
	public boolean inDistanceThreshold(){
		double current = pidcontroller.getPIDSource().pidGet();
		return Mathf.constrained(getDistanceThreshold() - current, -distanceMargin, distanceMargin);
	}
	
	public double getDistanceMargin(){
		return distanceMargin;
	}
	public void setDistanceMargin(double margin){
		distanceMargin = margin;
	}
	public double getDistanceThreshold(){
		return pidcontroller.getSetPoint().get();
	}
	
	public void setTimeInThreshold(int ms){
		timeInThreshold = ms;
	}
	public int getTimeInThreshold(){
		return timeInThreshold;
	}

	@Override
	public PIDController getPIDController(){
		return pidcontroller;
	}
}
