package edu.flash3388.flashlib.testing;

import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.CombinedAction;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;

public class TankOrientationCombinedAction extends CombinedAction{

	private TankDriveSystem driveTrain;
	private ActionPart positioning;
	private ActionPart rotation;
	private double minSpeed, maxSpeed;
	
	public TankOrientationCombinedAction(TankDriveSystem driveTrain, 
			ActionPart positioning, ActionPart rotation, 
			double minSpeed, double maxSpeed){
		this.driveTrain = driveTrain;
		this.positioning = positioning;
		this.rotation = rotation;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		
		System s = driveTrain.getSystem();
		if(s != null)
			requires(s);
	}
	public TankOrientationCombinedAction(TankDriveSystem driveTrain, 
			ActionPart positioning, ActionPart rotation){
		this(driveTrain, positioning, rotation, 0.1, 0.7);
	}
	
	@Override 
	protected void initialize() {
		super.initialize();
	}
	@Override
	protected void execute() {
		double speedY = Mathd.limit(positioning.getExecute(), minSpeed, maxSpeed);
		double speedX = Mathd.limit(rotation.getExecute(), minSpeed, maxSpeed);
		
		driveTrain.arcadeDrive(speedY, speedX);
	}
	@Override
	protected void end() {
		super.end();
		driveTrain.stop();
	}

	public void setPositioningActionPart(ActionPart pos){
		this.positioning = pos;
	}
	public ActionPart getPositioningActionPart(){
		return positioning;
	}
	public void setRotationActionPart(ActionPart rot){
		this.rotation = rot;
	}
	public ActionPart getRotationActionPart(){
		return rotation;
	}
	
	public double getMinSpeed(){
		return minSpeed;
	}
	public void setMinSpeed(double min){
		minSpeed = min;
	}
	public double getMaxSpeed(){
		return maxSpeed;
	}
	public void setMaxSpeed(double max){
		minSpeed = max;
	}
}
