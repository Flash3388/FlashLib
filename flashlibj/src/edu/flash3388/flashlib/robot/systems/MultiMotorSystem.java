package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.System;

public class MultiMotorSystem extends System implements YAxisMovable, XAxisMovable, Rotatable{

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
	
	private FlashSpeedController controllers;
	private double default_speed_forward = 0.5, default_speed_backward = 0.5;
	
	public MultiMotorSystem(FlashSpeedController controllers){
		this(controllers, null);
	}
	public MultiMotorSystem(FlashSpeedController controllers, Action defaultAction){
		super(null);
		this.controllers = controllers;
		
		if(defaultAction != null) 
			setDefaultAction(defaultAction);
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
	
	@Override
	public void forward(double speed){
		if(speed < 0) speed *= -1;
		controllers.set(speed);
	}
	public void forward(){
		forward(default_speed_forward);
	}
	
	@Override
	public void backward(double speed){
		if(speed < 0) speed *= -1;
		controllers.set(-speed);
	}
	public void backward(){
		backward(default_speed_backward);
	}
	@Override
	public void stop(){
		controllers.stop();
	}
	
	@Override
	protected void initDefaultAction() {}
	@Override
	public void rotate(double speed, boolean direction) {
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
	public void driveX(double speed, boolean direction) {
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
	public void driveY(double speed, boolean direction) {
		if(direction) forward(speed);
		else backward(speed);
	}
	@Override
	public System getSystem() {
		return this;
	}

}
