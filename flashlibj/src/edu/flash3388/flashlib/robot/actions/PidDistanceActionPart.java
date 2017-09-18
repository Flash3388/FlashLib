package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.PropertyAction;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class PidDistanceActionPart extends PropertyAction implements PidAction{

	private PidController pidcontroller;
	private double distanceMargin;
	
	public PidDistanceActionPart(PidController controller, double distanceMargin){
		this.distanceMargin = distanceMargin;
		this.pidcontroller = controller;
	}
	public PidDistanceActionPart(PidController controller){
		this(controller, 15.0);
	}
	public PidDistanceActionPart(PidSource source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource distanceThreshold, 
			double distanceMargin){
		this.distanceMargin = distanceMargin;
		
		pidcontroller = new PidController(kp, ki, kd, kf);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PidDistanceActionPart(PidSource source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	public PidDistanceActionPart(PidSource source, double kp, double ki, double kd, double kf, DoubleSource distanceThreshold, 
			double distanceMargin){
		this.distanceMargin = distanceMargin;
		
		pidcontroller = new PidController(kp, ki, kd, kf);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PidDistanceActionPart(PidSource source, double kp, double ki, double kd, double kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	
	@Override
	protected void initialize() {
		valueProperty().set(0);
		pidcontroller.setEnabled(true);
		pidcontroller.reset();
	}
	@Override
	public void execute() {
		if(!pidcontroller.isEnabled() || inDistanceThreshold())
			valueProperty().set(0);
		else {
			valueProperty().set(-pidcontroller.calculate());
		}
	}
	@Override
	protected boolean isFinished() {
		return inDistanceThreshold();
	}
	@Override
	protected void end() {
		valueProperty().set(0);
	}
	
	public boolean inDistanceThreshold(){
		double current = pidcontroller.getPIDSource().pidGet();
		return (current >= getDistanceThreshold() - distanceMargin && current <= getDistanceThreshold() + distanceMargin);
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

	@Override
	public PidController getPIDController(){
		return pidcontroller;
	}
}
