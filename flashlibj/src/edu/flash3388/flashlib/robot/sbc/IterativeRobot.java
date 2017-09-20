package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.HIDUpdateTask;

public abstract class IterativeRobot extends RobotBase implements Robot{
	
	public static final int ITERATION_DELAY = 5; //ms
	
	private boolean stop = false;
	private Scheduler schedulerImpl;
	private ModeSelector modeSelector;
	
	private void robotLoop(){
		if((Flashboard.getInitMode() & Flashboard.INIT_COMM) != 0)
			Flashboard.start();
		
		int lastState;
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
				lastState = getMode();
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_FULL);
				modeInit(lastState);
				
				while(lastState == getMode() && !FlashRobotUtil.inEmergencyStop()){
					MotorSafetyHelper.checkAll();
					schedulerImpl.run();
					modePeriodic(lastState);
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
		modeSelector = ainitializer.modeSelector;
		
		if(ainitializer.autoUpdateHid)
			schedulerImpl.addTask(new HIDUpdateTask());
		
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
	
	
	public ModeSelector getStateSelector(){
		return modeSelector;
	}
	public int getMode(){
		return modeSelector == null? ModeSelector.MODE_DISABLED : modeSelector.getMode();
	}
	public boolean isMode(int mode){
		return getMode() == mode;
	}
	
	@Override
	public boolean isDisabled(){
		return isMode(ModeSelector.MODE_DISABLED);
	}
	@Override
	public boolean isOperatorControl() {
		return false;
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
	
	protected abstract void modeInit(int mode);
	protected abstract void modePeriodic(int mode);
}
