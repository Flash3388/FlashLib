package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PIDController;
import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.robot.PropertyAction;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class PIDRotationActionPart extends PropertyAction implements PIDAction{

	private PIDController pidcontroller;
	private double rotationMargin;
	
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
	public PIDRotationActionPart(PIDSource source, double kp, double ki, double kd, double kf, DoubleSource distanceThreshold, 
			double rotationMargin){
		this.rotationMargin = rotationMargin;
		
		pidcontroller = new PIDController(kp, ki, kd, kf);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PIDRotationActionPart(PIDSource source, double kp, double ki, double kd, double kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	
	@Override
	protected void initialize() {
		set(0);
		pidcontroller.setEnabled(true);
		pidcontroller.reset();
	}
	@Override
	public void execute() {
		if(!pidcontroller.isEnabled() || inRotationThreshold())
			set(0);
		else set(pidcontroller.calculate());
	}
	@Override
	protected boolean isFinished() {
		return inRotationThreshold();
	}
	@Override
	protected void end() {
		set(0);
	}
	
	public boolean inRotationThreshold(){
		double current = pidcontroller.getPIDSource().pidGet();
		return current > 0 && 
		(current >= getRotationThreshold() - rotationMargin && current <= getRotationThreshold() + rotationMargin);
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
	
	@Override
	public PIDController getPIDController(){
		return pidcontroller;
	}
}