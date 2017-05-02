package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;

public class SpeedChangeAction extends Action{
	private static final double ERROR_MARGIN = 0.05;
	
	private double to_speed;
	private double change_speed;
	private int direction;
	private double error_margin;
	private FlashSpeedController controllers;
	
	private boolean end = false;
	
	public SpeedChangeAction(FlashSpeedController controllers, double toSpeed, double changeSpeed){
		this(controllers, toSpeed, changeSpeed, -1, null);
	}
	public SpeedChangeAction(FlashSpeedController controllers, double toSpeed, double changeSpeed, double timeout, System s){
		this.setTimeOut((long) (timeout * 1000));
		this.resetRequirements();
		this.requires(s);
		this.controllers = controllers;
		this.to_speed = toSpeed;
		this.change_speed = changeSpeed;
	}
	
	@Override
	protected void initialize(){
		end = false;
		double cSpeed = controllers.get();
		this.direction = (int) (cSpeed / Math.abs(cSpeed));
		this.error_margin = ERROR_MARGIN * direction;
	}
	@Override
	protected void execute() {
		double cSpeed = controllers.get();
		if(to_speed <= cSpeed - error_margin  && cSpeed + error_margin >= to_speed)
			end = true;
		
		controllers.set(cSpeed - (change_speed * direction));
	}
	@Override
	protected boolean isFinished(){
		return end;
	}
	@Override
	protected void end() {
		controllers.stop();
	}
}
