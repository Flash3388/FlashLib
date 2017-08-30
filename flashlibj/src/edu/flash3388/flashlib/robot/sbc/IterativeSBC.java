package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public abstract class IterativeSBC extends SBCRobotBase{

	private boolean stop = false;
	private Log log;
	
	@Override
	protected void startRobot() {
		log = FlashUtil.getLog();
		robotInit();
		
		byte lastObsState = -1;
		byte state = StateSelector.STATE_DISABLED;
		while (!stop) {
			if(FlashRobotUtil.inEmergencyStop()){
				log.save();
				log.log("NEW STATE - EMERGENCY STOP");
				disabledInit();
				
				while (!stop && FlashRobotUtil.inEmergencyStop()) {
					disabledPeriodic();
					FlashUtil.delay(5);
				}
				continue;
			}
			
			state = stateSelector().getState();
			if(lastObsState < 0 || state != lastObsState){
				log.save();
				log.logTime("NEW STATE - "+state);
				lastObsState = state;
				if(robot().isDisabled())
					disabledInit();
				else stateInit(state);
			}
			if(robot().isDisabled())
				disabledPeriodic();
			else {
				robot().scheduler().run();
				statePeriodic(state);
			}
			
			FlashUtil.delay(5);
		}
	}
	@Override
	protected void stopRobot(){
		stop = true;
		robotShutdown();
	}
	
	protected abstract void robotInit();
	protected abstract void robotShutdown();
	protected abstract void disabledInit();
	protected abstract void disabledPeriodic();
	protected abstract void stateInit(byte state);
	protected abstract void statePeriodic(byte state);
}
