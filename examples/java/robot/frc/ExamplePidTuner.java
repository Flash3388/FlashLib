package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.PidTuner;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.frc.IterativeFRCRobot;
import edu.flash3388.flashlib.robot.systems.SingleMotorSystem;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;

import edu.wpi.first.wpilibj.Encoder;

/*
 * An example for using the pid tunning and tracking of the flashboard. We will use pid to set the RPM of a 
 * rotating shooting system with one motor. Although it is possible to use any PID controller you wish, we 
 * will use the one provided by flashlib.
 */
public class ExamplePidTuner extends IterativeFRCRobot{

	SingleMotorSystem shooter;
	
	PidTuner pidtuner;
	PidController pidcontroller;
	
	Encoder encoder;
	
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
				new FRCSpeedControllers(motor)
		);
		
		/*
		 * Creates the encoder which measures the rotation of the wheel. Creates a double source pointing to
		 * the encoder rate to be used by the PID controller and pid tuner.
		 */
		encoder = new Encoder(0, 1);
		encoderValue = ()->encoder.getRate();
		
		/*
		 * Creates a new pid controller from flashlib. We use our data properties for the constants. The setpoint is double'
		 * source passed to it and the pid source is created for the encoderValue source we created.
		 */
		pidcontroller = new PidController(kp, ki, kd, kf, setpoint, 
				new PidSource.DoubleSourcePidSource(encoderValue));
		
		/*
		 * Creates a pid tuner for the flashboard using the double properties we created.
		 */
		pidtuner = new PidTuner("shooter", kp, ki, kd, kf, setpoint, encoderValue);
		
		/*
		 * Attaches the pidtuner to the flashboard
		 */
		Flashboard.attach(pidtuner);
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
