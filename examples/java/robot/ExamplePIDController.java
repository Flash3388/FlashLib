package examples.robot;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.FlashboardModeSelector;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.robot.PIDController;
import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.robot.devices.DigitalInput;
import edu.flash3388.flashlib.robot.devices.Encoder;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.IndexEncoder;
import edu.flash3388.flashlib.robot.devices.PulseCounter;
import edu.flash3388.flashlib.robot.devices.Talon;
import edu.flash3388.flashlib.robot.hal.HALDigitalInput;
import edu.flash3388.flashlib.robot.hal.HALPWM;
import edu.flash3388.flashlib.robot.hal.HALPulseCounter;
import edu.flash3388.flashlib.robot.systems.SingleMotorSystem;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.util.beans.PropertyHandler;

/*
 * In this example we will take a look at FlashLib's PID controller. 
 * We will use PID to rotate a single-motor firing system. Our feedback
 * device will be an encoder, we will use one which has an index channel.
 * 
 * To control our system's rotation we will place a slider on Flashboard.
 */
public class ExamplePIDController extends IterativeRobot{

	
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
		
		//attach our mode selector to flashboard
		modeSelector.attachToFlashboard();
		
		//let's initialize our subsystem. We will use a PWM speed controller
		//to control the shooter's motor. In this example we will use the Talon
		//speed controller with the built in FlashLib class. For PWM port, we will
		//use FlashLib's HAL:
		FlashSpeedController speedController = new Talon(new HALPWM(SHOOTER_MOTOR));
		shooter = new SingleMotorSystem(speedController);
		
		//let's initialize our encoder. We will use the IndexEncoder class from FlashLib.
		//The distance per pulse value for our encoder will depend on the wheel radius of
		//the shooter, let's assume the value. Since it's an index encoder, we only get one
		//pulse per revolution, so our distance per pulse is the wheel circumference:
		double distancePerPulse = 2 * Math.PI * SHOOTER_WHEEL_RADIUS;
		
		//For encoder ports, we will use FlashLib HAL:
		DigitalInput encoderInput = new HALDigitalInput(SHOOTER_ENCODER);
		PulseCounter encoderCounter = new HALPulseCounter(SHOOTER_ENCODER);
		encoder = new IndexEncoder(encoderInput, encoderCounter, distancePerPulse);
		
		/*
		 * The PIDController class allows us to control a system using a PID control loop.
		 * We have 4 control parameters:
		 * -kp: Proportional
		 * -ki: integral
		 * -kd: differential
		 * -kf: feed forward
		 * 
		 * We can also provide different settings for the controller to improve the operation
		 * of the system in many ways, such as:
		 * - output limits
		 * - input limits
		 * - change limits
		 * We will not review those in this example.
		 * 
		 * The input of the system is defined in a PIDSource interface object.
		 * It has a single method: pidGet, which should return the current status
		 * of the system.
		 * 
		 * The set point is defined as a DoubleSource object from FlashLib's beans package.
		 * Using this, we can easily manipulate the value of the set point throughout the robot
		 * code.
		 * 
		 * The control parameters are defined as DoubleProperty objects from
		 * FlashLib's beans package. This way, we can remotely set their values throughout our
		 * robot project.
		 * 
		 * There are 2 constructor types: property constructor and variable constructor.
		 * Property constructor receives the DoubleProperty objects for the control parameters.
		 * Variable constructor receives an initial parameter values and creates the property objects
		 * by it self. 
		 */
		
		//Let's talk about the PIDSource object. This should point to a sensor of sorts which measures our
		//system's status. In our case, it's the encoder we use. The FlashLib Encoder interface already
		//extends PIDSource, so we can just give it our encoder:
		PIDSource pidsource = encoder;
		
		//We should insure that the encoder returns a rate value and not distance value when 
		//used by PIDSource:
		encoder.setPIDType(Encoder.PIDType.Rate);
		
		//For the set point source, we will use a DoubleProperty (extends DoubleSource) created
		//from the constants handler: pidSetPoint
		
		//In this example we will use the variable constructor for our PIDController. Let's 
		//create it:
		pidcontroller = new PIDController(0.05, 0.0, 0.0, 0.0, pidSetPoint, pidsource);
		
		//Because we output our PID values to a motor, we can only have a specific
		//range of outputs. FlashLib motor's accept a range -1 to 1.
		//But since we only want to rotate our system in one direction: forward, so
		//it would shoot, we should limit our values between 0 and 1:
		pidcontroller.setOutputLimit(0.0, 1.0);
		
		//now we have our PID controller ready for use. 
		
		
		//now we should place our slider on Flashboard so we could control
		//the motors RPM. We will also place the encoder value as a graph by time:
		
		Flashboard.putSlider("RPM", pidSetPoint, 0.0, MAX_RPM, 1000);
		
		DoubleSource timeSource = ()->{
			return FlashUtil.secs();
		};
		Flashboard.putLineChart("Velocity", timeSource, encoder, 0.0, 5.0, 0.0, MAX_RPM);
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
