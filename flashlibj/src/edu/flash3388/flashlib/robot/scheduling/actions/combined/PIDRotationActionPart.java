package edu.flash3388.flashlib.robot.scheduling.actions.combined;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.control.PIDController;
import edu.flash3388.flashlib.robot.control.PIDSource;
import edu.flash3388.flashlib.robot.scheduling.PropertyAction;
import edu.flash3388.flashlib.robot.scheduling.PIDAction;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class PIDRotationActionPart extends PropertyAction implements PIDAction {

	private PIDController pidcontroller;
	private double rotationMargin;
	
	private int timeInThreshold = 0, thresholdStart = 0;
	
	public PIDRotationActionPart(PIDController controller, double rotationMargin){
		this.rotationMargin = rotationMargin;
		this.pidcontroller = controller;
	}
	public PIDRotationActionPart(PIDController controller){
		this(controller, 15.0);
	}
	public PIDRotationActionPart(PIDSource source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource distanceThreshold, 
			double rotationMargin){
		this.rotationMargin = rotationMargin;
		
		pidcontroller = new PIDController(kp, ki, kd, kf);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PIDRotationActionPart(PIDSource source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	public PIDRotationActionPart(PIDSource source, double kp, double ki, double kd, double kf, 
			DoubleSource distanceThreshold, double rotationMargin){
		this.rotationMargin = rotationMargin;
		
		pidcontroller = new PIDController(kp, ki, kd, kf);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PIDRotationActionPart(PIDSource source, double kp, double ki, double kd, double kf, 
			DoubleSource distanceThreshold){
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
		if(!pidcontroller.isEnabled() || inRotationThreshold()){
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
		return inRotationThreshold() && FlashUtil.millisInt() - thresholdStart >= timeInThreshold;
	}
	@Override
	protected void end() {
		set(0);
	}
	
	public boolean inRotationThreshold(){
		double current = pidcontroller.getPIDSource().pidGet();
		return Mathf.constrained(getRotationThreshold() - current, -rotationMargin, rotationMargin);
	}
	
	public double getRotationMargin(){
		return rotationMargin;
	}
	public void setRotationMargin(double margin){
		rotationMargin = margin;
	}
	public double getRotationThreshold(){
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