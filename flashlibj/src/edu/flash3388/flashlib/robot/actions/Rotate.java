package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.Rotatable;

public class Rotate extends Action{
	
	private Rotatable drive;
	private ModableMotor modable;
	private double speed;
	private boolean dir;
	
	public Rotate(Rotatable driveTrain, double speed, boolean direction, int millis){
		this.drive = driveTrain;
		this.speed = speed;
		this.dir = direction;
		
		setTimeOut(millis);
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		if(driveTrain instanceof ModableMotor)
			modable = (ModableMotor)driveTrain;
	}
	public Rotate(Rotatable driveTrain, double speed, boolean direction, double seconds){
		this(driveTrain, speed, direction, (int)(seconds * 1000));
	}
	public Rotate(Rotatable driveTrain, double speed, boolean direction){
		this(driveTrain, speed, direction, -1);
	}
	
	@Override
	protected void execute() {
		drive.rotate(speed, dir);
	}
	@Override
	protected void end() {
		drive.stop();
	}
	
	public void setSpeed(double speed){
		this.speed = speed;
	}
	public double getSpeed(){
		return speed;
	}
	public void setDirection(boolean direction){
		this.dir = direction;
	}
	public boolean getDirection(){
		return dir;
	}
	public void setModable(ModableMotor modable){
		this.modable = modable;
	}
}
