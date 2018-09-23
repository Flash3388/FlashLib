package edu.flash3388.flashlib.robot.scheduling;

import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.Time;

/**
 * An {@link Action} wrapper which calls {@link #execute()} once during a period of time defined by the user.
 * 
 * @author TomTzook
 * @since FlashLib 1.0.1
 */
public class PeriodicAction extends Action {

	private final Action mAction;
	private final Clock mClock;
	private final long mPeriodMs;

	private long mLastRunTimeMs;
	
	public PeriodicAction(Action action, Clock clock, long periodMs){
		mAction = action;
		mClock = clock;
		mPeriodMs = periodMs;
		
		setTimeoutMs(action.getTimeoutMs());
		copyRequirements(action);
	}

	@Override
	protected void initialize(){
		mAction.initialize();
		mLastRunTimeMs = Time.INVALID_TIME;
	}
	
	@Override
	protected void execute() {
		if(mLastRunTimeMs == Time.INVALID_TIME || mClock.currentTimeMillis() - mLastRunTimeMs >= mPeriodMs){
			mAction.execute();
			mLastRunTimeMs = mClock.currentTimeMillis();
		}
	}

	@Override
	protected boolean isFinished(){
		return mAction.isFinished();
	}
	
	@Override
	protected void end() {
		mAction.end();
	}
	
	@Override
	protected void interrupted(){
		mAction.interrupted();
	}

}
