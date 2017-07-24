package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.SourceAction;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;

public class PidRotationActionPart extends SourceAction implements PidAction{

	private PidController pidcontroller;
	private double rotationMargin;
	
	public PidRotationActionPart(PidSource source, double kp, double ki, double kd, DoubleDataSource rotationThreshold, 
			double rotationMargin){
		this.rotationMargin = rotationMargin;
		
		pidcontroller = new PidController(kp, ki, kd, 0);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(rotationThreshold);
	}
	public PidRotationActionPart(PidSource source, double kp, double ki, double kd){
		this(source, kp, ki, kd, ()->100.0, 15.0);
	}
	
	@Override
	protected void initialize() {
		dataSource.set(0);
		pidcontroller.setEnabled(true);
		pidcontroller.reset();
	}
	@Override
	public void execute() {
		if(!pidcontroller.isEnabled() || inRotationThreshold())
			dataSource.set(0);
		else dataSource.set(pidcontroller.calculate());
	}
	@Override
	protected boolean isFinished() {
		return inRotationThreshold();
	}
	@Override
	protected void end() {
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
	public double getRotationThreshold(){
		return pidcontroller.getSetPoint().get();
	}
	
	@Override
	public PidController getPidController(){
		return pidcontroller;
	}
}