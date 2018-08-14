package examples.robot.flashboard;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.FlashboardModeSelector;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.robot.PIDController;
import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.robot.io.devices.Encoder;
import edu.flash3388.flashlib.robot.io.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.io.devices.IndexEncoder;
import edu.flash3388.flashlib.robot.io.devices.Talon;
import edu.flash3388.flashlib.robot.io.devices.Encoder.EncoderDataType;
import edu.flash3388.flashlib.robot.systems.SingleMotorSystem;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.PropertyHandler;

/*
 * In this example we will take a look at the Flashboard PID tuner. 
 * We will use PID to rotate a single-motor firing system. Our feedback
 * device will be an encoder, we will use one which has an index channel.
 * 
 * The PID control will be monitored from Flashboard using the PID tuner and we would
 * be able to control its parameters from there.
 */
public class ExampleFlashboardPIDTuner extends IterativeRobot{

	
	//we will use a single custom operation mode which will 
	//indicate robot operation.
	public static final int MODE_OPERATION = 1;
	
	//Our shooter's wheel radius. This value will be used for
	//encoder configuration.
	public static final double SHOOTER_WHEEL_RADIUS = 0.1;//m
	
	//The maximum RPM of the motor
	public static final double MAX_RPM = 5000.0;
	
	//The shooter's motor PWM connection on our platform.
	public static final int SHOOTER_MOTOR = 0;
	//The encoder's digital input port on our platform.
	public static final int SHOOTER_ENCODER = 6;
	
	//our feedback device - an encoder sensor attached to the shooter wheel.
	Encoder encoder;
	//our shooter system, using a built-in class from flashlib.
	SingleMotorSystem shooter;
	
	//create our mode selector. 
	//we will use the flashboard mode selector control
	FlashboardModeSelector modeSelector = new FlashboardModeSelector();
	
	//our PID controller object
	PIDController pidcontroller;
	
	//a set point for the PID controller
	DoubleProperty pidSetPoint = PropertyHandler.putNumber("setpoint", 0.0);

	@Override
	protected void preInit(IterativeRobotInitializer initializer) {
		//since we use HAL PWM ports for controlling our drive system, we need to allow
		//initialization of the FlashLib HAL
		//By allowing HAL initialization the IOFactory implementation is set to an HAL provider.
		initializer.initHAL = true;
		
		//indicate that we want to initialize flashboard
		initializer.initFlashboard = true;
		//since we have no use in the flashboard's camera server, we disable it
		initializer.flashboardInitData.initMode = Flashboard.INIT_COMM;
		
		//set our mode selector
		initializer.modeSelector = modeSelector;
	}
	@Override
	protected void robotInit() {
		
		//let's initialize our subsystem. We will use a PWM speed controller
		//to control the shooter's motor. In this example we will use the Talon
		//speed controller with the built in FlashLib class. For PWM port, we will
		//use FlashLib's HAL. Since HAL was initialized, IOFactory
		//will provide ports from HAL, so we can just pass the port number to our controller
		//and it will use IOFactory to create the PWM port.
		FlashSpeedController speedController = new Talon(SHOOTER_MOTOR);
		shooter = new SingleMotorSystem(speedController);
		
		//let's initialize our encoder. We will use the IndexEncoder class from FlashLib.
		//The distance per pulse value for our encoder will depend on the wheel radius of
		//the shooter, let's assume the value. Since it's an index encoder, we only get one
		//pulse per revolution, so our distance per pulse is the wheel circumference:
		double distancePerPulse = 2 * Math.PI * SHOOTER_WHEEL_RADIUS;
		
		//For encoder ports, we will use FlashLib HAL. Since HAL was initialized, IOFactory
		//will provide ports from HAL, so we can just pass the port number to our encoder
		//and it will use IOFactory to create the pulse counter.
		encoder = new IndexEncoder(SHOOTER_ENCODER, distancePerPulse);
		
		/*
		 * Now we need to initialize our PIDController. It is not actually a must to 
		 * use FlashLib's PIDController with the Flashboard PID tuner, but in this example
		 * we will. 
		 * Check out example about PIDController to learn more about it.
		 */
		
		//We should insure that the encoder returns a rate value and not distance value when 
		//used by the PIDController
		encoder.setDataType(EncoderDataType.Rate);
		
		//For the set point source, we will use a DoubleProperty (extends DoubleSource) created
		//from the constants handler: pidSetPoint
		
		//In this example we will use the variable constructor for our PIDController. Let's 
		//create it:
		pidcontroller = new PIDController(0.05, 0.0, 0.0, 0.0, pidSetPoint, encoder);
		
		//Because we output our PID values to a motor, we can only have a specific
		//range of outputs. FlashLib motor's accept a range -1 to 1.
		//But since we only want to rotate our system in one direction: forward, so
		//it would shoot, we should limit our values between 0 and 1:
		pidcontroller.setOutputLimit(0.0, 1.0);
		
		//now we have our PID controller ready for use. 
		
		/*
		 * The FlashboardPIDTuner simply requires several beans objects in order to function.
		 * We will use properties from PIDController for that.
		 * 
		 * The PID tuner can control our set point and PID constants, so it will require their 
		 * DoubleProperty. While it also needs a DoubleSource which indicates the current system output
		 * which is our encoder.
		 * 
		 * In addition to those, the PID tuner can receive maximum constants value and constants slider ticks, 
		 * since it uses sliders for the ticks. So we will use 5.0 as max value with 1000 ticks.
		 */
		Flashboard.putPIDTuner("shooter", 
				pidcontroller.kpProperty(), pidcontroller.kiProperty(), pidcontroller.kdProperty(), 
				pidcontroller.kfProperty(), pidSetPoint, encoder, 
				5.0, 1000);
		
		//now our tuner is ready for use.
	}

	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}

	@Override
	protected void modeInit(int mode) {
		//it is important to reset the controller when using it each time. this
		//resets accumulated information from the last run:
		pidcontroller.reset();
		
		//we need to make sure the PID controller is enabled
		pidcontroller.setEnabled(true);
	}
	@Override
	protected void modePeriodic(int mode) {
		//we need to periodically use the PID controller to receive outputs.
		//calling the calculate method performs the PID calculation and returns an
		//output value to be used to fix our system.
		double pidvalue = pidcontroller.calculate();
		
		shooter.set(pidvalue);
	}
}
