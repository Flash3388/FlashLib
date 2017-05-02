package edu.flash3388.flashlib.robot.rio;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import static edu.flash3388.flashlib.robot.Scheduler.*;
import static edu.flash3388.flashlib.util.FlashUtil.*;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

import static edu.flash3388.flashlib.robot.rio.FlashRioUtil.*;

public abstract class FlashRio extends SampleRobot {
	
	private static final double WARNING_VOLTAGE = 8.5;
	private static final double POWER_DRAW_WARNING = 80.0;
	private static final long ITERATION_DELAY = 20;
	
	private Log log;
	private Log powerLog;
	private double powerDrawTotal = 0;
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
				
				while(isDisabled()){
					disabledPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InDisabled(false);
				powerDrawTotal += powerDrawState;
				log.logTime("DISABLED - DONE");
				powerLog.logTime("Done State: Disabled >> Voltage: "+m_ds.getBatteryVoltage() + " >> SCurrent: "+powerDrawState +
						" >> TCurrent: "+powerDrawTotal,
						powerLogTime());
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
				
				while(isEnabled() && isAutonomous()){
					runScheduler();
					autonomousPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InAutonomous(false);
				powerDrawTotal += powerDrawState;
				log.logTime("AUTONOMOUS - DONE");
				powerLog.logTime("Done State: Autonomous >> Voltage: "+m_ds.getBatteryVoltage() + " >> SCurrent: "+powerDrawState +
						" >> TCurrent: "+powerDrawTotal,
						powerLogTime());
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
				
				while(isEnabled() && isTest()){
					runScheduler();
					testPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InTest(false);
				powerDrawTotal += powerDrawState;
				log.logTime("TEST - DONE");
				powerLog.logTime("Done State: Test >> Voltage: "+m_ds.getBatteryVoltage() + " >> SCurrent: "+powerDrawState +
						" >> TCurrent: "+powerDrawTotal,
						powerLogTime());
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
				
				while(isEnabled() && isOperatorControl()){
					runScheduler();
					teleopPeriodic();
					logLowVoltage();
					delay(ITERATION_DELAY);
				}
				m_ds.InOperatorControl(false);
				powerDrawTotal += powerDrawState;
				log.logTime("TELEOP - DONE");
				powerLog.logTime("Done State: Teleop >> Voltage: "+m_ds.getBatteryVoltage() + " >> SCurrent: "+powerDrawState +
						" >> TCurrent: "+powerDrawTotal,
						powerLogTime());
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
	
	protected void setPowerLogging(boolean log) {
		logPower = log;
		if(!log)
			powerLog.disable();
		else powerLog.setLoggingMode(Log.MODE_FULL);
	}
	protected void setPowerDrawWarning(double current){
		warningPowerDraw = current;
	}
	protected void setVoltageDropWarning(double volts){
		warningVoltage = volts;
	}
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
