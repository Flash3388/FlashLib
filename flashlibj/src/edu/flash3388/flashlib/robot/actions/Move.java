package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.robot.systems.XAxisMovable;
import edu.flash3388.flashlib.robot.systems.YAxisMovable;

public class Move extends Action{

	private YAxisMovable ydrive;
	private XAxisMovable xdrive;
	private ModableMotor modable;
	private double speed;
	private boolean dir;
	
	public Move(YAxisMovable driveTrain, double speed, boolean dir, int millis){
		this.ydrive = driveTrain;
		this.speed = speed;
		this.dir = dir;
		
		setTimeOut(millis);
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		if(driveTrain instanceof ModableMotor)
			modable = (ModableMotor)driveTrain;
	}
	public Move(YAxisMovable driveTrain, double speed, boolean dir, double seconds){
		this(driveTrain, speed, dir, (int)(seconds * 1000));
	}
	public Move(YAxisMovable driveTrain, double speed, boolean dir){
		this(driveTrain, speed, dir, -1);
	}
	
	public Move(XAxisMovable driveTrain, double speed, boolean dir, int millis){
		this.xdrive = driveTrain;
		this.speed = speed;
		this.dir = dir;
		
		setTimeOut(millis);
		System s = null;
		if((s = driveTrain.getSystem()) != null)
			requires(s);
		if(driveTrain instanceof ModableMotor)
			modable = (ModableMotor)driveTrain;
	}
	public Move(XAxisMovable driveTrain, double speed, boolean dir, double seconds){
		this(driveTrain, speed, dir, (int)(seconds * 1000));
	}
	public Move(XAxisMovable driveTrain, double speed, boolean dir){
		this(driveTrain, speed, dir, -1);
	}
	
	private void stop(){
		if(ydrive != null) ydrive.stop();
		else xdrive.stop();
	}
	private void drive(double speed, boolean dir){
		if(ydrive != null) ydrive.driveY(speed, dir);
		else xdrive.driveX(speed, dir);
	}
	
	@Override
	protected void execute() {
		drive(speed, dir);
	}
	@Override
	protected void end() {
		stop();
	}
	
	public void setSpeed(double speed){
		this.speed = speed;
	}
	public double getSpeed(){
		return speed;
	}
	public void setDirection(boolean dir){
		this.dir = dir;
	}
	public boolean getDirection(){
		return dir;
	}
	public void setModable(ModableMotor modable){
		this.modable = modable;
	}
}
