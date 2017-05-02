package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.Rotatable;

public class Rotate extends Action{
	
	private Rotatable drive;
	private ModableMotor modable;
	private double speed;
	private int direction;
	
	public Rotate(Rotatable driveTrain, double speed, int direction, long millis){
		this.drive = driveTrain;
		this.speed = speed;
		this.direction = direction;
		
		setTimeOut(millis);
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		if(driveTrain instanceof ModableMotor)
			modable = (ModableMotor)driveTrain;
	}
	public Rotate(Rotatable driveTrain, double speed, int direction, double seconds){
		this(driveTrain, speed, direction, (long)(seconds * 1000));
	}
	public Rotate(Rotatable driveTrain, double speed, int direction){
		this(driveTrain, speed, direction, -1);
	}
	
	@Override
	protected void execute() {
		drive.rotate(speed, direction);
	}
	@Override
	protected void end() {
		if(modable != null && !modable.inBrakeMode())
			modable.enableBrakeMode(true);
		drive.stop();
		if(modable != null && modable.inBrakeMode())
			modable.enableBrakeMode(false);
	}
	
	public void setSpeed(double speed){
		this.speed = speed;
	}
	public double getSpeed(){
		return speed;
	}
	public void setDirection(int direction){
		this.direction = direction;
	}
	public double getDirection(){
		return direction;
	}
	public void setModable(ModableMotor modable){
		this.modable = modable;
	}
}
