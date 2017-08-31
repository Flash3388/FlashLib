package edu.flash3388.flashlib.robot.frc;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import static edu.flash3388.flashlib.util.FlashUtil.*;

import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.HIDInterface;
import edu.flash3388.flashlib.robot.HidUpdateTask;
import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.robot.Scheduler;

import static edu.flash3388.flashlib.robot.FlashRobotUtil.inEmergencyStop;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

/**
 * The base for FRC robots wanting to use FlashLib in its fullest. Provides a control loop with power
 * tracking and control modes. Manually initializes FlashLib to its full extent. Extends SampleRobot. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see SampleRobot
 */
public abstract class FlashFRC extends SampleRobot implements Robot{
	
	protected static class RobotInitializer{
		public boolean logsEnabled = false;
		public boolean autoUpdateHid = true;
		public boolean logPower = true;
		public double warningVoltage = WARNING_VOLTAGE;
		public double warningPowerDraw = POWER_DRAW_WARNING;
		
		public boolean initFlashboard = true;
		public FlashboardInitData flashboardInitData = new FlashboardInitData();
	}
	
	private static final double WARNING_VOLTAGE = 8.5;
	private static final double POWER_DRAW_WARNING = 80.0;
	private static final byte ITERATION_DELAY = 10;
	
	private Log log, powerLog;
	private double warningVoltage;
	private double warningPowerDraw;
	private boolean logPower, logsEnabled;
	
	private Scheduler schedulerImpl;
	private HIDInterface hidImpl;
	
	@Override
	protected final void robotInit(){
		RobotInitializer initializer = new RobotInitializer();
		preInit(initializer);
		
		schedulerImpl = new Scheduler();
		hidImpl = new FRCHidInterface();
		
		FlashFRCUtil.initFlashLib(this, initializer.initFlashboard? initializer.flashboardInitData : null);
		
		log = FlashUtil.getLog();
		logPower = initializer.logPower;
		logsEnabled = initializer.logsEnabled;
		if(initializer.logsEnabled){
			powerLog = Log.createBufferedLog("power");
			if(!logPower)
				powerLog.disable();
		}else{
			logPower = false;
			log.disable();
			log.delete();
		}
		
		if(initializer.autoUpdateHid)
			schedulerImpl.addTask(new HidUpdateTask());
		
		warningPowerDraw = initializer.warningPowerDraw;
		warningVoltage = initializer.warningVoltage;
		
		initRobot();
		log.logTime("Robot initialized");
	}
	@Override
	public final void robotMain() {
		log.logTime("STARTING");
		LiveWindow.setEnabled(false);
		if(logPower)
			powerLog.logTime("Starting Voltage: "+m_ds.getBatteryVoltage(), "Robot", powerLogTime());
		
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
				m_ds.InDisabled(true);
				disabledInit();
				
				while(isDisabled() && !inEmergencyStop()){
					disabledPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InDisabled(false);
			}else if(isAutonomous()){
				logNewState("Autonomous");
				
				schedulerImpl.removeAllActions();
				m_ds.InAutonomous(true);
				autonomousInit();
				
				while(isEnabled() && isAutonomous() && !inEmergencyStop()){
					schedulerImpl.run();
					autonomousPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InAutonomous(false);
			}else if(isTest()){
				logNewState("Test");
				
				schedulerImpl.removeAllActions();
				m_ds.InTest(true);
				testInit();
				
				while(isEnabled() && isTest() && !inEmergencyStop()){
					schedulerImpl.run();
					testPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InTest(false);
			}else{
				logNewState("Teleop");
				
				schedulerImpl.removeAllActions();
				m_ds.InOperatorControl(true);
				teleopInit();
				
				while(isEnabled() && isOperatorControl() && !inEmergencyStop()){
					schedulerImpl.run();
					teleopPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InOperatorControl(false);
			}
		}
	}
	
	private double powerLogTime(){
		double matchTime = m_ds.getMatchTime();
		return matchTime > 0? matchTime : FlashUtil.secs();
	}
	private void logLowVoltage(){
		if(!logPower) return;
		
		double volts = m_ds.getBatteryVoltage();
		double matchTime = powerLogTime();
		double powerDraw = FlashFRCUtil.getPDP().getTotalCurrent();
		boolean emergencySave = false;
		if(volts < warningVoltage){
			powerLog.logTime("Low Voltage: "+volts, "Robot", matchTime);
			emergencySave = true;
		}
		if(m_ds.isBrownedOut()){
			powerLog.logTime("Browned Out", "Robot", matchTime);
			emergencySave = true;
		}
		if(powerDraw >= warningPowerDraw){
			powerLog.logTime("High Draw: "+powerDraw, "Robot", matchTime);
		}
		if(emergencySave){
			powerLog.save();
			log.save();
		}
	}
	private void logNewState(String state){
		if(!logsEnabled)
			return;
		log.logTime("NEW STATE - "+state);
		powerLog.logTime("New State: "+state+" >> Voltage: "+m_ds.getBatteryVoltage(), "Robot",
				powerLogTime());
		log.save();
		powerLog.save();
	}
	
	/**
	 * Sets whether or not to log data about power usage into a log.
	 * @param log true to log data, false otherwise
	 */
	protected void setPowerLogging(boolean log) {
		logPower = log;
		if(powerLog != null){
			if(!log)
				powerLog.disable();
			else 
				powerLog.setLoggingMode(Log.MODE_FULL);
		}
	}
	/**
	 * Sets the total power draw which should prompt a warning from the power log.
	 * @param current total current in Ampere
	 */
	protected void setPowerDrawWarning(double current){
		warningPowerDraw = current;
	}
	/**
	 * Sets the voltage level which should prompt a warning from the power log.
	 * @param volts voltage in Volts
	 */
	protected void setVoltageDropWarning(double volts){
		warningVoltage = volts;
	}
	/**
	 * Gets the log used to log power data. If logs were not enabled, this will return null.
	 * @return the power log
	 */
	protected Log getPowerLog(){
		return powerLog;
	}

	
	@Override
	public Scheduler scheduler() {
		return schedulerImpl;
	}
	@Override
	public HIDInterface hid() {
		return hidImpl;
	}
	@Override
	public boolean isFRC() {
		return true;
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
