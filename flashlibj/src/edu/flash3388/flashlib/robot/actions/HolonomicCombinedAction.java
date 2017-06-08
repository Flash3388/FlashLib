package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.CombinedAction;
import edu.flash3388.flashlib.robot.SourceAction;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;

public class HolonomicCombinedAction extends CombinedAction{

	private HolonomicDriveSystem driveTrain;
	private SourceAction positioning;
	private SourceAction rotation;
	private double minSpeed, maxSpeed;
	private boolean rotate = true;
	
	public HolonomicCombinedAction(HolonomicDriveSystem driveTrain, 
			SourceAction positioning, SourceAction rotation, 
			double minSpeed, double maxSpeed, boolean rotate){
		this.driveTrain = driveTrain;
		this.positioning = positioning;
		this.rotation = rotation;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.rotate = rotate;
		
		add(rotation);
		add(positioning);
		
		System s = driveTrain.getSystem();
		if(s != null)
			requires(s);
	}
	public HolonomicCombinedAction(HolonomicDriveSystem driveTrain, 
			SourceAction positioning, SourceAction rotation, boolean rotate){
		this(driveTrain, positioning, rotation, 0.1, 0.7, rotate);
	}
	
	@Override 
	protected void initialize() {
		super.initialize();
	}
	@Override
	protected void execute() {
		super.execute();
		
		double speedY = positioning != null? 
				Mathd.limit2(positioning.getSource().get(), minSpeed, maxSpeed) : 0;
		double speedX = rotation != null? 
				Mathd.limit2(rotation.getSource().get(), minSpeed, maxSpeed) : 0;
		
		if(rotate)
			driveTrain.holonomicCartesian(0, speedY, speedX);
		else driveTrain.holonomicCartesian(speedX, speedY, 0);
	}
	@Override
	protected void end() {
		super.end();
		driveTrain.stop();
	}

	public void setPositioningActionPart(SourceAction pos){
		remove(positioning);
		this.positioning = pos;
		add(positioning);
	}
	public SourceAction getPositioningActionPart(){
		return positioning;
	}
	public void setRotationActionPart(SourceAction rot){
		remove(rotation);
		this.rotation = rot;
		add(rotation);
	}
	public SourceAction getRotationActionPart(){
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
		maxSpeed = max;
	}
}
