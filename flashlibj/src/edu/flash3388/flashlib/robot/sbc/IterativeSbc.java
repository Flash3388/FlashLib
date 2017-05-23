package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.util.FlashUtil;

public abstract class IterativeSbc extends SbcBot{

	private boolean stop = false;
	
	@Override
	protected void startRobot() {
		robotInit();
		
		byte lastObsState = -1;
		byte state = STATE_DISABLED;
		while (!stop) {
			state = getCurrentState();
			if(lastObsState < 0 || state != lastObsState){
				FlashUtil.getLog().save();
				FlashUtil.getLog().logTime("NEW STATE - "+state);
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
