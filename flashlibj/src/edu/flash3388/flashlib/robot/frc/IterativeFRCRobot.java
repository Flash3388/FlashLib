package edu.flash3388.flashlib.robot.frc;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import static edu.flash3388.flashlib.util.FlashUtil.*;

import java.util.logging.Logger;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.HIDUpdateTask;
import edu.flash3388.flashlib.robot.PowerLogger;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.robot.devices.IOFactory;

import static edu.flash3388.flashlib.robot.FlashRobotUtil.inEmergencyStop;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * IterativeFRCRobot provides the recommended base for FRC robots using FlashLib's robot framework.
 * This base provides a similar robot loop to WPILib's {@link edu.wpi.first.wpilibj.IterativeRobot IterativeRobot} 
 * and FlashLib's {@link edu.flash3388.flashlib.robot.IterativeRobot IterativeRobot}.
 * <p>
 * The control loop divides each operation mode into two types
 * <ul>
 * 	<li> init: initialization of the operation mode</li>
 *  <li> periodic: execution of the operation mode</li>
 * </ul>
 * `init` is called every time the robot enters a new mode. `periodic` is called every ~10ms while the robot
 * is in the operation mode.
 * <p>
 * Users extending this class must implement:
 * <ul>
 * 	<li> {@link #initRobot()}: initialization of robot systems
 * 	<li> {@link #disabledInit()}: initialization for disabled mode </li>
 * 	<li> {@link #disabledPeriodic()}: execution of disabled mode </li>
 *  <li> {@link #autonomousInit()}: initialization for autonomous operation mode </li>
 * 	<li> {@link #autonomousPeriodic()}: execution of a autonomous operation mode </li>
 *  <li> {@link #teleopInit()}: initialization for teleop operation mode </li>
 * 	<li> {@link #teleopPeriodic()}: execution of a teleop operation mode </li>
 * </ul>
 * {@link #initRobot()} is called after FlashLib systems finished initialization and are ready to be used.
 * Use this to initialize robot systems.
 * <p>
 * {@link #testInit()} and {@link #testPeriodic()} are provided for FRC's test mode. They have a default empty 
 * implementation and can be overridden if necessary.
 * <p>
 * Each iteration of the control loop puts the current thread into sleep for {@value #ITERATION_DELAY} milliseconds.
 * <p>
 * The scheduling system is updated by the control loop to allow operation of that system. While the robot
 * is in a mode, the {@link Scheduler#run()} method is executed periodically, insuring correct operation
 * of that system. When operation modes change, all {@link Action} objects are interrupted by calling 
 * {@link Scheduler#removeAllActions()} so that unwanted execution will not remain and cause issues. In
 * addition, when in disabled mode the scheduling enters {@link Scheduler#MODE_TASKS} mode so {@link Action} objects
 * are not executed, only tasks are, this is for safety of operation.
 * <p>
 * IterativeFRCRobot features power tracking using a {@link PowerLogger} object. If initialized, this object
 * tracks power issues in the robot's power supply. By default, the tracked values are the robot's voltage level
 * from {@link DriverStation#getBatteryVoltage()} and the total PDP power from {@link PDP#getTotalCurrent()} using
 * {@link FlashFRCUtil#getPDP()}. If issues are detected with the values of either ones, data is logged into a power
 * log. Issues refer to the values been too low or high.
 * <p>
 * This class provides custom initialization. When the robot is initializing, {@link #preInit(RobotInitializer)}
 * is called for custom initialization. The passed object, {@link RobotInitializer} provides variables whose
 * values are used to initialize FlashLib and control loop operations.
 * <p>
 * If flashboard was initialized, {@link Flashboard#start()} is called automatically.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * 
 * @see SampleRobot
 */
public abstract class IterativeFRCRobot extends FRCRobotBase{
	
	/**
	 * This class contains initialization parameters for {@link IterativeFRCRobot}.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.1
	 */
	protected static class RobotInitializer{
		/**
		 * Indicates whether or not the control loop should run the {@link Scheduler}.
		 * <p>
		 * The default value is `false`.
		 */
		public boolean runScheduler = true;
		
		/**
		 * Indicates whether or not to add an auto HID update task to the {@link Scheduler}. This will
		 * refresh HID data automatically, allowing for HID-activated actions. The task will update 
		 * controllers only if the current operation mode is teleop.
		 * <p>
		 * The default value is `false`.
		 */
		public boolean autoUpdateHid = true;
		
		/**
		 * Indicates whether or not the control loop should check robot's power
		 * status and log any problems into the power log. Uses an instance of {@link PowerLogger}.
		 * <p>
		 * The default value is `false`.
		 */
		public boolean logPower = false;
		/**
		 * Indicates to the power tracker what current draw in Ampre to consider high enough to warrant
		 * a warning.
		 * <p>
		 * The default value is 120A
		 */
		public double maxTotalCurrentDraw = PowerLogger.DEFAULT_WARNING_CURRENT_DRAW;
		/**
		 * Indicates to the power tracker what voltage level in volts to consider low enough to warrant
		 * a warning. 
		 * <p>
		 * Rge default value is 8.0v
		 */
		public double minVoltageLevel = PowerLogger.DEFAULT_WARNING_VOLTAGE;
		
		/**
		 * Indicates whether or not the robot should log operations into FlashLib's standard logs.
		 * If this value is false, FlashLib's main log's file writing is disabled and the file
		 * is deleted. The main log is retrieved from {@link FlashUtil#getLogger()}.
		 * <p>
		 * The default value is `false`.
		 */
		public boolean standardLogs = false;
		
		/**
		 * Contains initialization data for Flashboard in the form of {@link FlashboardInitData}.
		 * If this value is `null`, Flashboard control will not be initialized.
		 * <p>
		 * The default value is an instance of {@link FlashboardInitData}.
		 */
		public FlashboardInitData flashboardInitData = new FlashboardInitData();
		/**
		 * Inidicates whether or not to initialize flashboard. If true, flashboard will 
		 * be initialize. If false, flashboard will be initialized.
		 * <p>
		 * The default value is `true`.
		 */
		public boolean initFlashboard = true;
	}
	
	private static final int ITERATION_DELAY = 5;
	
	private Logger logger;
	private PowerLogger powerLogger;
	private Scheduler schedulerImpl = Scheduler.getInstance();
	
	private boolean runScheduler;
	private boolean logPower;
	private boolean stdLog;
	
	private void runScheduler(){
		if(runScheduler)
			schedulerImpl.run();
	}
	private void logPower(){
		if(logPower)
			powerLogger.logPower();
	}
	private void logNewState(String state){
		if(!stdLog)
			return;
		logger.info("NEW STATE - "+state);
	}
	
	@Override
	protected final void robotInit(){
		RobotInitializer initializer = new RobotInitializer();
		preInit(initializer);
		
		schedulerImpl = Scheduler.getInstance();
		
		FlashFRCUtil.initFlashLib(this, initializer.initFlashboard? initializer.flashboardInitData : null);
		
		IOFactory.setProvider(new FRCIOProvider());
		
		FRCVoltagePowerSource voltageSource = new FRCVoltagePowerSource(initializer.minVoltageLevel, 13.7);
		FlashRobotUtil.setVoltageSource(voltageSource);
		
		logger = FlashUtil.getLogger();
		
		stdLog = initializer.standardLogs;
		logPower = initializer.logPower;
		
		if(!stdLog){
			//TODO: DISABLE LOGGER
		}
		if(logPower){
			powerLogger = new PowerLogger("powerLog", voltageSource,
					new FRCTotalCurrentPowerSource(-1.0, initializer.maxTotalCurrentDraw));
		}
		if(initializer.autoUpdateHid){
			schedulerImpl.addTask(new HIDUpdateTask());
		}
		
		runScheduler = initializer.runScheduler;
		
		initRobot();
		logger.info("Robot initialized");
	}
	@Override
	public final void robotMain() {
		logger.info("STARTING");
		
		if((Flashboard.getInitMode() & Flashboard.INIT_COMM) != 0)
			Flashboard.start();
		
		LiveWindow.setEnabled(false);
		
		while(true){
			if(inEmergencyStop()){
				logNewState("EMERGENCY STOP");
				disabledInit();
				
				while (inEmergencyStop()) {
					disabledPeriodic();
					delay(50);
				}
			}
			if(isDisabled()){
				logNewState("Disabled");
				
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_TASKS);
				m_ds.InDisabled(true);
				disabledInit();
				
				while(isDisabled() && !inEmergencyStop()){
					runScheduler();
					disabledPeriodic();
					logPower();
					delay(ITERATION_DELAY);
				}
				m_ds.InDisabled(false);
			}else if(isAutonomous()){
				logNewState("Autonomous");
				
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_FULL);
				m_ds.InAutonomous(true);
				autonomousInit();
				
				while(isEnabled() && isAutonomous() && !inEmergencyStop()){
					runScheduler();
					autonomousPeriodic();
					logPower();
					delay(ITERATION_DELAY);
				}
				m_ds.InAutonomous(false);
			}else if(isTest()){
				logNewState("Test");
				
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_FULL);
				m_ds.InTest(true);
				testInit();
				
				while(isEnabled() && isTest() && !inEmergencyStop()){
					runScheduler();
					testPeriodic();
					logPower();
					delay(ITERATION_DELAY);
				}
				m_ds.InTest(false);
			}else{
				logNewState("Teleop");
				
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_FULL);
				m_ds.InOperatorControl(true);
				teleopInit();
				
				while(isEnabled() && isOperatorControl() && !inEmergencyStop()){
					runScheduler();
					teleopPeriodic();
					logPower();
					delay(ITERATION_DELAY);
				}
				m_ds.InOperatorControl(false);
			}
		}
	}
	
	/**
	 * Gets the {@link PowerLogger} object used by this class to log power issues. If
	 * the power logger was not initialized, this method will throw a {@link IllegalStateException}.
	 * Whether or not power logging is specified by {@link RobotInitializer#logPower}.
	 * 
	 * @return the {@link PowerLogger} object
	 * @throws IllegalStateException if power logging was not initialized
	 */
	protected PowerLogger getPowerLogger(){
		if(!logPower)
			throw new IllegalStateException("PowerLogger was not initialized");
		return powerLogger;
	}
	
	/**
	 * Called just before initialization of FlashLib. Useful to perform pre-initialization settings.
	 * @param initializer the initialization data
	 */
	protected void preInit(RobotInitializer initializer){}
	/**
	 * Called after initialization of FlashLib. Use this to initialize your robot systems and actions for use.
	 */
	protected abstract void initRobot();
	/**
	 * Called once when entering Disabled mode. Use this to disable your robot.
	 */
	protected abstract void disabledInit();
	/**
	 * Called periodically while in Disabled mode. Can be used to display data.
	 */
	protected abstract void disabledPeriodic();
	/**
	 * Called once when entering Teleoperation mode. Use this to initialize your robot for operator control.
	 */
	protected abstract void teleopInit();
	/**
	 * Called periodically while in Teleoperation mode. Use this to perform actions during operator control.
	 */
	protected abstract void teleopPeriodic();
	/**
	 * Called once when entering Autonomous mode. Use this to initialize your robot for automatic control.
	 */
	protected abstract void autonomousInit();
	/**
	 * Called periodically while in Autonomous mode. Use this to perform actions during automatic control.
	 */
	protected abstract void autonomousPeriodic();
	/**
	 * Called once when entering Test mode. Use this to initialize your robot for test control.
	 */
	protected void testInit(){}
	/**
	 * Called periodically while in Test mode. Use this to perform actions during test control.
	 */
	protected void testPeriodic(){}
}
