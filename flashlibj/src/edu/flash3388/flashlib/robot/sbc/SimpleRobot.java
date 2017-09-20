package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.util.FlashUtil;

public abstract class SimpleRobot extends RobotBase implements Robot{
	
	public static final int ITERATION_DELAY = 5; //ms
	
	private boolean stop = false;
	private ModeSelector modeSelector;
	
	private void robotLoop(){
		if((Flashboard.getInitMode() & Flashboard.INIT_COMM) != 0)
			Flashboard.start();
		
		int lastState;
		while(!stop){
			if(FlashRobotUtil.inEmergencyStop()){
				
				while(FlashRobotUtil.inEmergencyStop()){
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
			else if(isDisabled()){
				disabled();
				
				while(isDisabled() && !FlashRobotUtil.inEmergencyStop()){
					FlashUtil.delay(ITERATION_DELAY);
				}
			}else{
				lastState = getMode();
				
				onMode(lastState);
				
				while(lastState == getMode() && !FlashRobotUtil.inEmergencyStop()){
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
		}
	}
	
	@Override
	protected void configInit(BasicInitializer initializer){
		RobotInitializer ainitializer = new RobotInitializer();
		preInit(ainitializer);
		
		FlashRobotUtil.initFlashLib(this, ainitializer.hidImpl, ainitializer.flashboardInitData);
		modeSelector = ainitializer.modeSelector;
		
		initializer.copy(ainitializer);
	}
	@Override
	protected void robotMain() {
		robotLoop();
	}
	@Override
	protected void robotShutdown(){
		stop = true;
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
	
	protected abstract void disabled();
	
	protected abstract void onMode(int mode);
}
