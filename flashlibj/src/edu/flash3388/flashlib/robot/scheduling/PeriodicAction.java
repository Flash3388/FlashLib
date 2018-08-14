package edu.flash3388.flashlib.robot.scheduling;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An {@link Action} wrapper which calls {@link #execute()} once during a period of time defined by the user.
 * 
 * @author TomTzook
 * @since FlashLib 1.0.1
 */
public class PeriodicAction extends Action {

	private Action mAction;
	private int mPeriodMs;
	private int mLastRunTimeMs;
	
	public PeriodicAction(Action action, int periodMs){
		this.mAction = action;
		this.mPeriodMs = periodMs;
		
		setTimeoutMs(action.getTimeoutMs());
		copyRequirements(action);
	}

	public PeriodicAction(Action action, double periodSeconds){
		this(action, (int)(periodSeconds * 0.001));
	}
	
	@Override
	protected void initialize(){
		mAction.initialize();
		mLastRunTimeMs = -1;
	}
	
	@Override
	protected void execute() {
		if(mLastRunTimeMs < 0 || FlashUtil.millisInt() - mLastRunTimeMs >= mPeriodMs){
			mAction.execute();
			mLastRunTimeMs = FlashUtil.millisInt();
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
