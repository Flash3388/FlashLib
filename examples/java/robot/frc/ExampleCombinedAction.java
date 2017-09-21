package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.robot.actions.GyroRotationActionPart;
import edu.flash3388.flashlib.robot.actions.PidDistanceActionPart;
import edu.flash3388.flashlib.robot.actions.PidRotationActionPart;
import edu.flash3388.flashlib.robot.actions.TankCombinedAction;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.frc.IterativeFRCRobot;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.FlashDrive;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Ultrasonic;

public class ExampleCombinedAction extends IterativeFRCRobot{

	FlashDrive driveTrain;
	
	XboxController controller;
	
	Ultrasonic sonic;
	DoubleSource sonicData;
	AnalogGyro agyro;
	Gyro gyro;
	
	DoubleProperty kp = ConstantsHandler.putNumber("kp", 0.0);
	DoubleProperty ki = ConstantsHandler.putNumber("ki", 0.0);
	DoubleProperty kd = ConstantsHandler.putNumber("kd", 0.0);
	DoubleProperty kf = ConstantsHandler.putNumber("kf", 0.0);
	DoubleProperty rsetpoint = ConstantsHandler.putNumber("rsetpoint", 0.0);
	DoubleProperty dsetpoint = ConstantsHandler.putNumber("dsetpoint", 0.0);
	
	protected void initRobot() {
		/*
		 * Creates all the speed controllers used for the drive system.
		 * Luckily for us, we use TalonSRX controllers that are connected through CANBus.
		 */
		CANTalon frontRight = new CANTalon(0);
		CANTalon frontLeft = new CANTalon(1);
		CANTalon rearRight = new CANTalon(2);
		CANTalon rearLeft = new CANTalon(3);
		
		/*
		 * Creating the drive. Wrapping controllers from the same side into a RioControllers object.
		 */
		driveTrain = new FlashDrive(
				new FRCSpeedControllers(frontRight, rearRight), 
				new FRCSpeedControllers(frontLeft, rearLeft)
		);
		
		/*
		 * Creating an xbox controller at index 0
		 */
		controller = new XboxController(0);
		
		/*
		 * Create a gyroscope and define a gyro interface to point to it 
		 */
		agyro = new AnalogGyro(1);
		gyro = ()->agyro.getAngle();
		
		/*
		 * Create an ultrasonic and define a double source to point to it
		 */
		sonic = new Ultrasonic(0, 1);
		sonicData = ()->sonic.getRangeMM();
		
		/*
		 * Create the rotation part of the combined action. There are several built-in options to use
		 * mostly using PID controllers, we will see them all now.
		 */
		/*
		 * Create a general class which uses a PID loop to perform the rotation part. By using different PidSource we
		 * get a different type of rotation.
		 * The vision pid source provides several pid sources for vision. It uses the basic analysis data and 
		 * can provide data for rotation or linear motion. We have configured it to use the value of PROP_HORIZONTAL_DISTANCE
		 * from the Analysis class. We also used the vision built into flashboard.
		 */
		PIDSource visionRotation = new PIDSource.VisionPIDSource(Flashboard.getVision(), true, false);
		PidRotationActionPart ractionpart = new PidRotationActionPart(visionRotation, kp, ki, kd, kf, rsetpoint, 5.0);
		/*
		 * Rotation to an angle using a gyro. This class allows 2 angle rotation types:
		 * - Relative: rotates a given angle relative to the current one. i.e the current angle is considered as 0.
		 * - Absolute: rotates to a given angle relative to the gyro-defined 0 angle. 
		 */
		GyroRotationActionPart gyroRotation = new GyroRotationActionPart(gyro, kp, ki, kd, kf, rsetpoint, 5.0);
		
		/*
		 * Create a general class which uses a PID loop to perform the distance part. By using different PidSource we
		 * get a different type of distance changing.
		 * We will use an ultrasonic sensor to define the distance from a target to approach or back from. The action will
		 * attempt to reach a certain distance from the target an remain within the given margin.
		 */
		PIDSource sonicSource = new PIDSource.DoubleSourcePIDSource(sonicData);
		PidDistanceActionPart dactionpart = new PidDistanceActionPart(sonicSource, kp, ki, kd, kf, dsetpoint, 15.0);
		
		/*
		 * We can create a combined action for tankdrives and use the action parts we have created. Just use this like
		 * any other action. It is possible to pass null for one of the parts, nothing will be done with that part then.
		 */
		TankCombinedAction combinedAction = new TankCombinedAction(driveTrain, dactionpart, gyroRotation);
		
		controller.A.whenPressed(combinedAction);
	}

	@Override
	protected void teleopInit() {
	}
	@Override
	protected void teleopPeriodic() {
	}

	@Override
	protected void autonomousInit() {}
	@Override
	protected void autonomousPeriodic() {}

	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}
}
