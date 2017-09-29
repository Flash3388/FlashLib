package examples.robot.fullrobot;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/*
 * An action which drives a TankDriveSystem using the arcadeDrive method. That method has a default
 * implementation in the TankDriveSystem interface. It calculates the values for each side of the 
 * drive train and calls tankDrive.
 * 
 * We use 2 DoubleSource objects from the beans package to allow for dynamically changing 
 * speed value.
 */
public class ArcadeDriveAction extends Action{

	//the drive train object
	private TankDriveSystem driveTrain;
	//the speed source objects
	private DoubleSource moveSpeed, rotateSpeed;
	
	//constructor 1: receive the drive train object and the speed sources, save them
	public ArcadeDriveAction(TankDriveSystem driveTrain, DoubleSource moveSpeedSource, 
			DoubleSource rotateSpeedSource) {
		this.driveTrain = driveTrain;
		this.rotateSpeed = rotateSpeedSource;
		this.moveSpeed = moveSpeedSource;
	}
	//constructor 2: receive the drive train object and two double variable which will provide for constant
	//movement speed. We call the first constructor and turn the speed variables into DoubleSource objects.
	public ArcadeDriveAction(TankDriveSystem driveTrain, double moveSpeed, double rotateSpeed){
		this(driveTrain, ()->moveSpeed, ()->rotateSpeed);
	}
	
	@Override
	protected void execute() {
		//call the arcadeDrive method of the drive train, 
		//access the values of the speed sources and pass them to the method.
		driveTrain.arcadeDrive(moveSpeed.get(), rotateSpeed.get());
	}
	@Override
	protected void end() {
		//stop the drive train by calling the stop method.
		driveTrain.stop();
	}
}
