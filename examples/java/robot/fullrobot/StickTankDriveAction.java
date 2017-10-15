package examples.robot.fullrobot;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.hid.Axis;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;

/*
 * An action which drives a TankDriveSystem using the tankDrive method. 
 * 
 * We use 2 Axis objects of a controller to move the system
 */
public class StickTankDriveAction extends Action{

	//the drive train object
	private TankDriveSystem driveTrain;
	//the HID axis objects
	private Axis leftAxis, rightAxis;
	
	//constructor 1: receive the drive train object and the axes, save them
	public StickTankDriveAction(TankDriveSystem driveTrain, Axis right, Axis left) {
		this.driveTrain = driveTrain;
		this.leftAxis = left;
		this.rightAxis = right;
	}
	
	@Override
	protected void execute() {
		//call the tankDrive method of the drive train, 
		//access the values of the axes and pass them to the method.
		driveTrain.tankDrive(rightAxis.get(), leftAxis.get());
	}
	@Override
	protected void end() {
		//stop the drive train by calling the stop method.
		driveTrain.stop();
	}

}
