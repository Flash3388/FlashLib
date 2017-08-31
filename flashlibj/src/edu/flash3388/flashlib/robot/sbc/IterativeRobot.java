package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.HIDInterface;

public abstract class IterativeRobot extends RobotBase implements Robot{

	protected static class RobotInitializer{
		public HIDInterface hidImpl;
		public StateSelector stateSelector;
	}
	
	public static final int ITERATION_DELAY = 5; //ms
	
	private boolean stop = false;
	private Scheduler schedulerImpl;
	private HIDInterface hidImpl;
	private StateSelector stateSelector;
	
	private void initialize(){
		schedulerImpl = new Scheduler();
		
		RobotInitializer initializer = new RobotInitializer();
		preInit(initializer);
		
		hidImpl = initializer.hidImpl;
		stateSelector = initializer.stateSelector;
	}
	private void robotLoop(){
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
				disabledInit();
				
				while(FlashRobotUtil.inEmergencyStop()){
					disabledPeriodic();
					FlashUtil.delay(ITERATION_DELAY);
				}
			}else{
				lastState = getState();
				schedulerImpl.removeAllActions();
				stateInit(lastState);
				
				while(lastState == getState()){
					statePeriodic(lastState);
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
		}
	}
	
	@Override
	protected void startRobot() {
		initialize();
		robotLoop();
	}
	@Override
	protected void stopRobot(){
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
	public Scheduler getScheduler(){
		return schedulerImpl;
	}
	@Override
	public HIDInterface getHIDInterface(){
		return hidImpl;
	}
	@Override
	public boolean isDisabled(){
		return getState() == StateSelector.STATE_DISABLED;
	}
	@Override
	public boolean isFRC(){
		return false;
	}
	
	protected void robotShutdown(){}
	protected abstract void preInit(RobotInitializer initializer);
	protected abstract void robotInit();
	protected abstract void disabledInit();
	protected abstract void disabledPeriodic();
	protected abstract void stateInit(byte state);
	protected abstract void statePeriodic(byte state);
}
