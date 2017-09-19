package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.EmptyHIDInterface;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.HIDInterface;

public abstract class IterativeRobot extends RobotBase implements Robot{

	protected static class RobotInitializer extends BasicInitializer{
		public HIDInterface hidImpl = new EmptyHIDInterface();
		public StateSelector stateSelector;
		public FlashboardInitData flashboardInitData = new FlashboardInitData();
		
		public void copy(RobotInitializer initializer){
			super.copy(initializer);
			hidImpl = initializer.hidImpl;
			stateSelector = initializer.stateSelector;
			flashboardInitData = initializer.flashboardInitData;
		}
	}
	
	public static final int ITERATION_DELAY = 5; //ms
	
	private boolean stop = false;
	private Scheduler schedulerImpl;
	private StateSelector stateSelector;
	
	private void robotLoop(){
		if((Flashboard.getInitMode() & Flashboard.INIT_COMM) != 0)
			Flashboard.start();
		
		byte lastState;
		while(!stop){
			if(FlashRobotUtil.inEmergencyStop()){
				disabledInit();
				
				while(FlashRobotUtil.inEmergencyStop()){
					disabledPeriodic();
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
			else if(isDisabled()){
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_TASKS);
				MotorSafetyHelper.disableAll();
				disabledInit();
				
				while(isDisabled() && !FlashRobotUtil.inEmergencyStop()){
					disabledPeriodic();
					FlashUtil.delay(ITERATION_DELAY);
				}
			}else{
				lastState = getState();
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_FULL);
				stateInit(lastState);
				
				while(lastState == getState() && !FlashRobotUtil.inEmergencyStop()){
					MotorSafetyHelper.checkAll();
					schedulerImpl.run();
					statePeriodic(lastState);
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
		}
	}
	
	@Override
	protected void configInit(BasicInitializer initializer){
		schedulerImpl = Scheduler.getInstance();
		
		RobotInitializer ainitializer = new RobotInitializer();
		preInit(ainitializer);
		
		FlashRobotUtil.initFlashLib(this, ainitializer.hidImpl, ainitializer.flashboardInitData);
		stateSelector = ainitializer.stateSelector;
		
		initializer.copy(ainitializer);
	}
	@Override
	protected void robotMain() {
		robotLoop();
	}
	@Override
	protected void robotShutdown(){
		stop = true;
		schedulerImpl.removeAllActions();
		schedulerImpl.setDisabled(true);
		robotShutdown();
	}
	
	
	public StateSelector getStateSelector(){
		return stateSelector;
	}
	public byte getState(){
		return stateSelector == null? StateSelector.STATE_DISABLED : stateSelector.getState();
	}

	
	@Override
	public boolean isDisabled(){
		return getState() == StateSelector.STATE_DISABLED;
	}
	@Override
	public boolean isFRC(){
		return false;
	}
	

	protected void preInit(RobotInitializer initializer){}
	protected abstract void robotInit();
	protected void robotFree(){}
	
	protected abstract void disabledInit();
	protected abstract void disabledPeriodic();
	
	protected abstract void stateInit(byte state);
	protected abstract void statePeriodic(byte state);
}
