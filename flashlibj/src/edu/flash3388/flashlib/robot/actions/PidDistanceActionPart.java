package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.SourceAction;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;

public class PidDistanceActionPart extends SourceAction implements PidAction{

	private PidController pidcontroller;
	private double distanceMargin;
	
	public PidDistanceActionPart(PidSource source, double kp, double ki, double kd, DoubleDataSource distanceThreshold, 
			double distanceMargin){
		this.distanceMargin = distanceMargin;
		
		pidcontroller = new PidController(kp, ki, kd);
		pidcontroller.setPIDSource(source);
		pidcontroller.setSetPoint(distanceThreshold);
	}
	public PidDistanceActionPart(PidSource source, double kp, double ki, double kd){
		this(source, kp, ki, kd, ()->100.0, 15.0);
	}
	
	@Override
	protected void initialize() {
		dataSource.set(0);
	}
	@Override
	public void execute() {
		if(inDistanceThreshold())
			dataSource.set(0);
		else {
			dataSource.set(-pidcontroller.calculate());
		}
	}
	@Override
	protected boolean isFinished() {
		return inDistanceThreshold();
	}
	@Override
	protected void end() {
	}
	
	public boolean inDistanceThreshold(){
		double current = pidcontroller.getSource().pidGet();
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
	public PidController getPidController(){
		return pidcontroller;
	}
}
