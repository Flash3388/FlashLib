package edu.flash3388.flashlib.robot.rio;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import static edu.flash3388.flashlib.util.FlashUtil.*;

import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.robot.RobotState;

import static edu.flash3388.flashlib.robot.FlashRoboUtil.inEmergencyStop;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

import static edu.flash3388.flashlib.robot.rio.FlashRioUtil.*;

/**
 * The base for FRC robots wanting to use FlashLib in its fullest. Provides a control loop with power
 * tracking and control modes. Manually initializes FlashLib to its full extent. Extends SampleRobot. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see SampleRobot
 */
public abstract class FlashRio extends SampleRobot {
	
	private static class HidScheduledTask implements Runnable{
		@Override
		public void run() {
			if(RobotState.isRobotTeleop())
				FlashRoboUtil.updateHID();
		}
	}
	protected static class RobotInitializer{
		public boolean logsEnabled = false;
		public boolean autoUpdateHid = true;
		public boolean logPower = true;
		public double warningVoltage = WARNING_VOLTAGE;
		public double warningPowerDraw = POWER_DRAW_WARNING;
	}
	
	private static final double WARNING_VOLTAGE = 8.5;
	private static final double POWER_DRAW_WARNING = 80.0;
	private static final byte ITERATION_DELAY = 10;
	
	private Log log;
	private Log powerLog;
	private double warningVoltage;
	private double warningPowerDraw;
	private boolean logPower, logsEnabled;
	
	
	@Override
	protected final void robotInit(){
		RobotInitializer initializer = new RobotInitializer();
		preInit(initializer);
		initFlashLib();
		
		log = FlashUtil.getLog();
		logPower = initializer.logPower;
		logsEnabled = initializer.logsEnabled;
		if(initializer.logsEnabled){
			powerLog = FlashUtil.createLog("powerlog");
			if(!logPower)
				powerLog.disable();
		}else{
			logPower = false;
			log.disable();
			log.delete();
		}
		
		if(initializer.autoUpdateHid)
			RobotFactory.getScheduler().addTask(new HidScheduledTask());
		
		initRobot();
		log.logTime("Robot initialized");
	}
	@Override
	public final void robotMain() {
		log.logTime("STARTING");
		LiveWindow.setEnabled(false);
		if(logPower)
			powerLog.logTime("Starting Voltage: "+m_ds.getBatteryVoltage(), powerLogTime());
		
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
				
				RobotFactory.getScheduler().removeAllActions();
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
				
				RobotFactory.getScheduler().removeAllActions();
				m_ds.InAutonomous(true);
				autonomousInit();
				
				while(isEnabled() && isAutonomous() && !inEmergencyStop()){
					RobotFactory.runScheduler();
					autonomousPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InAutonomous(false);
			}else if(isTest()){
				logNewState("Test");
				
				RobotFactory.getScheduler().removeAllActions();
				m_ds.InTest(true);
				testInit();
				
				while(isEnabled() && isTest() && !inEmergencyStop()){
					RobotFactory.runScheduler();
					testPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InTest(false);
			}else{
				logNewState("Teleop");
				
				RobotFactory.getScheduler().removeAllActions();
				m_ds.InOperatorControl(true);
				teleopInit();
				
				while(isEnabled() && isOperatorControl() && !inEmergencyStop()){
					RobotFactory.runScheduler();
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
		double powerDraw = FlashRioUtil.getPDP().getTotalCurrent();
		boolean emergencySave = false;
		if(volts < warningVoltage){
			powerLog.logTime("Low Voltage: "+volts, matchTime);
			emergencySave = true;
		}
		if(m_ds.isBrownedOut()){
			powerLog.logTime("Browned Out", matchTime);
			emergencySave = true;
		}
		if(powerDraw >= warningPowerDraw){
			powerLog.logTime("High Draw: "+powerDraw, matchTime);
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
		powerLog.logTime("New State: "+state+" >> Voltage: "+m_ds.getBatteryVoltage(),
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
	
	/**
	 * Called just before initialization of FlashLib. Useful to perform pre-initialization settings.
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
