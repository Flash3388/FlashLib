package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.VoltageScalable;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.System;

public class SingleMotorSystem extends System implements XAxisMovable, YAxisMovable, Rotatable, VoltageScalable{
	
	public final Action FORWARD_ACTION = new SystemAction(this, new Action(){
		@Override
		protected void execute() {forward();}
		@Override
		protected void end() {stop();}
	});
	public final Action BACKWARD_ACTION = new SystemAction(this, new Action(){
		@Override
		protected void execute() {backward();}
		@Override
		protected void end() { stop();}
	});
	public final Action STOP_ACTION = new SystemAction(this, new Action(){
		@Override
		protected void initialize(){ stop();}
		@Override
		protected void execute() { }
		@Override 
		protected boolean isFinished() {return true;}
		@Override
		protected void end() { stop();}
	});
	
	private FlashSpeedController controller;
	private double default_speed_forward = 0.5, default_speed_backward = 0.5;
	private boolean scaleVoltage = false;
	
	public SingleMotorSystem(FlashSpeedController controller){
		this(controller, null);
	}
	public SingleMotorSystem(FlashSpeedController controller, Action defaultAction){
		super(null);
		this.controller = controller;
		setDefaultAction(defaultAction);
	}
	
	public void setInverted(boolean inverted){
		controller.setInverted(inverted);
	}
	public boolean isInverted(){
		return controller.isInverted();
	}
	public FlashSpeedController getMotorController(){
		return controller;
	}
	
	public void setDefaultSpeed(double speed){
		setDefaultSpeed(speed, speed);
	}
	public void setDefaultSpeed(double forward, double backward){
		if(forward < 0) forward *= -1;
		if(backward < 0) backward *= -1;
				
		default_speed_forward = forward;
		default_speed_backward = backward;
	}
	
	private void set(double speed){
		double scaled = FlashRoboUtil.scaleVoltageBus(speed);
		controller.set(scaleVoltage? scaled : speed);
	}
	
	@Override
	public void forward(double speed){
		if(speed < 0) speed *= -1;
		set(speed);
	}
	public void forward(){
		forward(default_speed_forward);
	}
	@Override
	public void backward(double speed){
		if(speed < 0) speed *= -1;
		set(-speed);
	}
	public void backward(){
		backward(default_speed_backward);
	}
	@Override
	public void stop(){
		controller.set(0);
	}
	
	@Override
	protected void initDefaultAction() {}
	
	@Override
	public void rotate(double speed, int direction) {
		driveY(speed, direction);
	}
	@Override
	public void rotateRight(double speed) {
		forward(speed);
	}
	@Override
	public void rotateLeft(double speed) {
		backward(speed);
	}
	@Override
	public void driveX(double speed, int direction) {
		driveY(speed, direction);
	}
	@Override
	public void right(double speed) {
		forward(speed);
	}
	@Override
	public void left(double speed) {
		backward(speed);
	}
	@Override
	public void driveY(double speed, int direction) {
		if(direction > 0) forward(speed);
		else backward(speed);
	}
	@Override
	public System getSystem() {
		return this;
	}
	@Override
	public void enableVoltageScaling(boolean en) {
		scaleVoltage = en;
	}
	@Override
	public boolean isVoltageScaling() {
		return scaleVoltage;
	}
}
