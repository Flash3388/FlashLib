package edu.flash3388.flashlib.testing;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.CombinedAction;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;

public class HolonomicOrientationCombinedAction extends CombinedAction{

	private HolonomicDriveSystem driveTrain;
	private ActionPart positioning;
	private ActionPart rotation;
	private double minSpeed, maxSpeed;
	private boolean rotate = true;
	
	public HolonomicOrientationCombinedAction(HolonomicDriveSystem driveTrain, 
			ActionPart positioning, ActionPart rotation, 
			double minSpeed, double maxSpeed, boolean rotate){
		this.driveTrain = driveTrain;
		this.positioning = positioning;
		this.rotation = rotation;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.rotate = rotate;
		
		System s = driveTrain.getSystem();
		if(s != null)
			requires(s);
	}
	public HolonomicOrientationCombinedAction(HolonomicDriveSystem driveTrain, 
			ActionPart positioning, ActionPart rotation, boolean rotate){
		this(driveTrain, positioning, rotation, 0.1, 0.7, rotate);
	}
	
	@Override 
	protected void initialize() {
		super.initialize();
	}
	@Override
	protected void execute() {
		double speedY = Mathd.limit(positioning.getExecute(), minSpeed, maxSpeed);
		double speedX = Mathd.limit(rotation.getExecute(), minSpeed, maxSpeed);
		
		if(rotate)
			driveTrain.holonomicCartesian(0, speedY, speedX);
		else driveTrain.holonomicCartesian(speedX, speedY, 0);
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
	
	public boolean isSetToRotate(){
		return rotate;
	}
	public void setRotate(boolean rotate){
		this.rotate = rotate;
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
