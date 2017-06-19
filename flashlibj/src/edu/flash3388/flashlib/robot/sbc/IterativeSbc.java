package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.RobotState;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public abstract class IterativeSbc extends SbcBot{

	private boolean stop = false;
	private Log log;
	
	@Override
	protected void startRobot() {
		log = FlashUtil.getLog();
		robotInit();
		
		byte lastObsState = -1;
		byte state = STATE_DISABLED;
		while (!stop) {
			if(RobotState.inEmergencyStop()){
				log.save();
				log.log("NEW STATE - EMERGENCY STOP");
				disabledInit();
				
				while (!stop && RobotState.inEmergencyStop()) {
					disabledPeriodic();
					FlashUtil.delay(5);
				}
				continue;
			}
			
			state = getCurrentState();
			if(lastObsState < 0 || state != lastObsState){
				log.save();
				log.logTime("NEW STATE - "+state);
				lastObsState = state;
				if(isDisabled())
					disabledInit();
				else stateInit(state);
			}
			if(isDisabled())
				disabledPeriodic();
			else {
				if(Scheduler.schedulerHasInstance())
					Scheduler.runScheduler();
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
