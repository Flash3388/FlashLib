package edu.flash3388.flashlib.robot.rio;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import static edu.flash3388.flashlib.robot.Scheduler.*;
import static edu.flash3388.flashlib.util.FlashUtil.*;
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
	
	private static final double WARNING_VOLTAGE = 8.5;
	private static final double POWER_DRAW_WARNING = 80.0;
	private static final byte ITERATION_DELAY = 20;
	
	private Log log;
	private Log powerLog;
	private double powerDrawState = 0;
	private double warningVoltage = WARNING_VOLTAGE;
	private double warningPowerDraw = POWER_DRAW_WARNING;
	private boolean logPower = true;
	
	
	@Override
	protected void robotInit(){
		initFlashLib();
		log = FlashUtil.getLog();
		powerLog = new Log("powerlog");
		initRobot();
		log.logTime("Robot initialized");
	}
	@Override
	public void robotMain() {
		log.logTime("STARTING");
		LiveWindow.setEnabled(false);
		powerLog.logTime("Starting Voltage: "+m_ds.getBatteryVoltage(), powerLogTime());
		while(true){
			if(inEmergencyStop()){
				log.logTime("NEW STATE - EMERGENCY STOP");
				powerLog.logTime("New State: EMERGENCY STOP >> Voltage: "+m_ds.getBatteryVoltage(),
						powerLogTime());
				disabledInit();
				
				while (inEmergencyStop()) {
					disabledPeriodic();
					delay(50);
				}
				
				log.logTime("EMERGENCY STOP - DONE");
				powerLog.logTime("Done State: EMERGENCY STOP >> Voltage: "+m_ds.getBatteryVoltage() + 
						" >> SCurrent: "+powerDrawState, powerLogTime());
			}
			if(isDisabled()){
				log.logTime("NEW STATE - DISABLED");
				powerLog.logTime("New State: Disabled >> Voltage: "+m_ds.getBatteryVoltage(),
						powerLogTime());
				log.save();
				powerLog.save();
				powerDrawState = 0;
				disableScheduler(true);
				m_ds.InDisabled(true);
				disabledInit();
				
				while(isDisabled() && !inEmergencyStop()){
					disabledPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InDisabled(false);
				log.logTime("DISABLED - DONE");
				powerLog.logTime("Done State: Disabled >> Voltage: "+m_ds.getBatteryVoltage() + 
						" >> SCurrent: "+powerDrawState, powerLogTime());
			}else if(isAutonomous()){
				log.logTime("NEW STATE - AUTONOMOUS");
				powerLog.logTime("New State: Autonomous >> Voltage: "+m_ds.getBatteryVoltage(),
						powerLogTime());
				log.save();
				powerLog.save();
				powerDrawState = 0;
				disableScheduler(false);
				m_ds.InAutonomous(true);
				autonomousInit();
				
				while(isEnabled() && isAutonomous() && !inEmergencyStop()){
					runScheduler();
					autonomousPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InAutonomous(false);
				log.logTime("AUTONOMOUS - DONE");
				powerLog.logTime("Done State: Disabled >> Voltage: "+m_ds.getBatteryVoltage() + 
						" >> SCurrent: "+powerDrawState, powerLogTime());
			}else if(isTest()){
				log.logTime("NEW STATE - TEST");
				powerLog.logTime("New State: Test >> Voltage: "+m_ds.getBatteryVoltage(),
						powerLogTime());
				log.save();
				powerLog.save();
				powerDrawState = 0;
				disableScheduler(false);
				m_ds.InTest(true);
				testInit();
				
				while(isEnabled() && isTest() && !inEmergencyStop()){
					runScheduler();
					testPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InTest(false);
				log.logTime("TEST - DONE");
				powerLog.logTime("Done State: Disabled >> Voltage: "+m_ds.getBatteryVoltage() + 
						" >> SCurrent: "+powerDrawState, powerLogTime());
			}else{
				log.logTime("NEW STATE - TELEOP");
				powerLog.logTime("New State: Teleop >> Voltage: "+m_ds.getBatteryVoltage(),
						powerLogTime());
				log.save();
				powerLog.save();
				powerDrawState = 0;
				disableScheduler(false);
				m_ds.InOperatorControl(true);
				teleopInit();
				
				while(isEnabled() && isOperatorControl() && !inEmergencyStop()){
					runScheduler();
					teleopPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InOperatorControl(false);
				log.logTime("TELEOP - DONE");
				powerLog.logTime("Done State: Disabled >> Voltage: "+m_ds.getBatteryVoltage() + 
						" >> SCurrent: "+powerDrawState, powerLogTime());
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
		powerDrawState += powerDraw;
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
	
	/**
	 * Sets whether or not to log data about power usage into a log.
	 * @param log true to log data, false otherwise
	 */
	protected void setPowerLogging(boolean log) {
		logPower = log;
		if(!log)
			powerLog.disable();
		else powerLog.setLoggingMode(Log.MODE_FULL);
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
	 * Gets the log used to log power data.
	 * @return
	 */
	protected Log getPowerLog(){
		return powerLog;
	}
	
	protected abstract void initRobot();
	protected abstract void teleopInit();
	protected abstract void teleopPeriodic();
	protected abstract void autonomousInit();
	protected abstract void autonomousPeriodic();
	protected abstract void disabledInit();
	protected abstract void disabledPeriodic();
	protected void testInit(){}
	protected void testPeriodic(){}
}
