package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.HIDInterface;
import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public abstract class SimpleIterativeFRCRobot extends SampleRobot implements Robot{

	private Scheduler schedulerImpl = new Scheduler();
	private HIDInterface hidImpl = new FRCHidInterface();
	
	private boolean disabledInitialized = false, autonomousInitialized = false, teleopInitialized = false,
			testInitialized = false;
	
	@Override
	protected final void robotInit(){
		FlashFRCUtil.initFlashLib(this, null);
		Log.deleteLogFolder();
	}
	@Override
	public final void robotMain() {
		LiveWindow.setEnabled(false);
		
		while(true){
			
			 m_ds.waitForData();
			
			if(FlashRobotUtil.inEmergencyStop()){
				disabledInit();
				
				while(FlashRobotUtil.inEmergencyStop()){
					disabledPeriodic();
					FlashUtil.delay(100);
				}
			}
			else if(isDisabled()){
				if(!disabledInitialized){
					disabledInitialized = true;
					autonomousInitialized = false;
					testInitialized = false;
					teleopInitialized = false;
					
					schedulerImpl.setMode(Scheduler.MODE_TASKS);
					schedulerImpl.removeAllActions();
					
					disabledInit();
				}
				schedulerImpl.run();
				disabledPeriodic();
			}else if(isAutonomous()){
				if(!autonomousInitialized){
					disabledInitialized = false;
					autonomousInitialized = true;
					testInitialized = false;
					teleopInitialized = false;
					
					schedulerImpl.setMode(Scheduler.MODE_FULL);
					schedulerImpl.removeAllActions();
					
					autonomousInit();
				}
				schedulerImpl.run();
				autonomousPeriodic();
			}else if(isTest()){
				if(!testInitialized){
					disabledInitialized = false;
					autonomousInitialized = false;
					testInitialized = true;
					teleopInitialized = false;
					
					schedulerImpl.setMode(Scheduler.MODE_FULL);
					schedulerImpl.removeAllActions();
					
					testPeriodic();
				}
				schedulerImpl.run();
				testPeriodic();
			}else{
				if(!teleopInitialized){
					disabledInitialized = false;
					autonomousInitialized = false;
					testInitialized = false;
					teleopInitialized = true;
					
					schedulerImpl.setMode(Scheduler.MODE_FULL);
					schedulerImpl.removeAllActions();
					
					teleopInit();
				}
				schedulerImpl.run();
				teleopPeriodic();
			}
		}
	}
	
	@Override
	public Scheduler getScheduler() {
		return schedulerImpl;
	}
	@Override
	public HIDInterface getHIDInterface() {
		return hidImpl;
	}
	@Override
	public boolean isFRC() {
		return true;
	}
	
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
