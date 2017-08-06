package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.PidTuner;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.rio.AMT10Encoder;
import edu.flash3388.flashlib.robot.rio.FlashRio;
import edu.flash3388.flashlib.robot.rio.RioControllers;
import edu.flash3388.flashlib.robot.systems.SingleMotorSystem;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/*
 * An example for using the pid tunning and tracking of the flashboard. We will use pid to set the RPM of a 
 * rotating shooting system with one motor. Although it is possible to use any PID controller you wish, we 
 * will use the one provided by flashlib.
 */
public class ExamplePidTuner extends FlashRio{

	SingleMotorSystem shooter;
	
	XboxController controller;
	
	PidTuner pidtuner;
	PidController pidcontroller;
	
	DoubleProperty kp = ConstantsHandler.putNumber("kp", 0.0);
	DoubleProperty ki = ConstantsHandler.putNumber("ki", 0.0);
	DoubleProperty kd = ConstantsHandler.putNumber("kd", 0.0);
	DoubleProperty kf = ConstantsHandler.putNumber("kf", 0.0);
	DoubleProperty setpoint = ConstantsHandler.putNumber("setpoint", 0.0);
	DoubleSource encoderValue;
	
	protected void initRobot() {
		/*
		 * Creates the talon used to control the motor of our firing system
		 */
		CANTalon motor = new CANTalon(0);
		
		/*
		 * Creating the shooter system. Wraps the speed controller in a RioControllers object. 
		 */
		shooter = new SingleMotorSystem(
				new RioControllers(motor)
		);
		
		/*
		 * Creates the encoder which measures the rotation of the wheel. The model used is AMT10 which has a custom 
		 * class in flashlib. We will create it in an incremental state only, not quadrature.
		 */
		AMT10Encoder encoder = new AMT10Encoder(0);
		encoder.setAutomaticUpdate(true);
		encoderValue = ()->encoder.getRate();
		
		/*
		 * Creates a new pid controller from flashlib. Default values are used for the ks. The setpoint is double'
		 * source passed to it and the pid source is created for the encoderValue source we created.
		 */
		pidcontroller = new PidController(0.0, 0.0, 0.0, 0.0, setpoint, 
				new PidSource.DoubleDataPidSource(encoderValue));
		
		/*
		 * Creates a pid tuner for the flashboard using the double properties we created.
		 */
		pidtuner = new PidTuner("shooter", kp, ki, kd, kf, setpoint, encoderValue);
		
		/*
		 * Creating an xbox controller at index 0
		 */
		controller = new XboxController(0);
		
		/*
		 * Attaches the pidtuner to the flashboard
		 */
		Flashboard.attach(pidtuner);
		/*
		 * Starts the flashboard
		 */
		Flashboard.start();
	}

	@Override
	protected void teleopInit() {
		/*
		 * Resets the pid controller and enables it
		 */
		pidcontroller.reset();
		pidcontroller.setEnabled(true);
	}
	@Override
	protected void teleopPeriodic() {
		/*
		 * If the pid tuner is used on the flashboard
		 */
		if(pidtuner.isEnabled()){
			/*
			 * Update the pid controller values by the properties of the tuner
			 */
			pidcontroller.setPID(kp.get(), ki.get(), kd.get(), kf.get());
			
			/*
			 * Get the value calculated from the pid controller and constrain it between 0 and 1
			 */
			double val = Mathf.constrain(pidcontroller.calculate(), 0.0, 1.0);
			shooter.set(val);
		}
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
