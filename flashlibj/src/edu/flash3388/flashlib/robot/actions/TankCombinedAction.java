package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.CombinedAction;
import edu.flash3388.flashlib.robot.SourceAction;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;

public class TankCombinedAction extends CombinedAction{

	private TankDriveSystem driveTrain;
	private SourceAction positioning;
	private SourceAction rotation;
	private double minSpeed, maxSpeed;
	
	public TankCombinedAction(TankDriveSystem driveTrain, 
			SourceAction positioning, SourceAction rotation, 
			double minSpeed, double maxSpeed){
		this.driveTrain = driveTrain;
		this.positioning = positioning;
		this.rotation = rotation;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		
		add(rotation);
		add(positioning);
		
		if(driveTrain instanceof Subsystem)
			requires((Subsystem)driveTrain);
	}
	public TankCombinedAction(TankDriveSystem driveTrain, 
			SourceAction positioning, SourceAction rotation){
		this(driveTrain, positioning, rotation, 0.1, 0.7);
	}
	
	@Override 
	protected void initialize() {
		super.initialize();
	}
	@Override
	protected void execute() {
		super.execute();
		
		double speedY = positioning != null? 
				Mathf.constrain2(positioning.get(), minSpeed, maxSpeed) : 0.0;
		double speedX = rotation != null? 
				Mathf.constrain2(rotation.get(), minSpeed, maxSpeed) : 0.0;
		
		driveTrain.arcadeDrive(speedY, speedX);
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
