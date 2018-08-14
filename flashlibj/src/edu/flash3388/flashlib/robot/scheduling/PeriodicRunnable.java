package edu.flash3388.flashlib.robot.scheduling;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * A {@link Runnable} wrapper which calls {@link #run()} once during a period of time defined by the user.
 * 
 * @author TomTzook
 * @since FlashLib 1.0.1
 */
public class PeriodicRunnable implements Runnable {

	private Runnable mRunnable;
	private int mPeriodMs;
	private int mLastRunTimeMs = -1;
	
	public PeriodicRunnable(Runnable task, int periodMs){
		this.mRunnable = task;
		this.mPeriodMs = periodMs;
	}

	public PeriodicRunnable(Runnable task, double periodSeconds){
		this(task, (int)(periodSeconds * 0.001));
	}
	
	@Override
	public void run() {
		if(mLastRunTimeMs < 0 || FlashUtil.millisInt() - mLastRunTimeMs >= mPeriodMs){
			mRunnable.run();
			mLastRunTimeMs = FlashUtil.millisInt();
		}
	}
}
