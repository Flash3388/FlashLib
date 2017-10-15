package examples.robot.fullrobot;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/*
 * An action which drives a TankDriveSystem using the tankDrive method. 
 * 
 * We use 2 DoubleSource objects from the beans package to allow for dynamically changing 
 * speed value.
 */
public class TankDriveAction extends Action{

	//the drive train object
	private TankDriveSystem driveTrain;
	//the speed source objects
	private DoubleSource speedR, speedL;
	
	//constructor 1: receive the drive train object and the speed sources, save them
	public TankDriveAction(TankDriveSystem driveTrain, DoubleSource speedSourceR, DoubleSource speedSourceL) {
		this.driveTrain = driveTrain;
		this.speedL = speedSourceL;
		this.speedR = speedSourceR;
	}
	//constructor 2: receive the drive train object and two double variable which will provide for constant
	//movement speed. We call the first constructor and turn the speed variables into DoubleSource objects.
	public TankDriveAction(TankDriveSystem driveTrain, double speedR, double speedL){
		this(driveTrain, ()->speedR, ()->speedL);
	}
	
	@Override
	protected void execute() {
		//call the tankDrive method of the drive train, 
		//access the values of the speed sources and pass them to the method.
		driveTrain.tankDrive(speedR.get(), speedL.get());
	}
	@Override
	protected void end() {
		//stop the drive train by calling the stop method.
		driveTrain.stop();
	}
}
