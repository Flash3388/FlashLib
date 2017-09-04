package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * A {@link Runnable} wrapper which calls {@link #run()} once during a period of time defined by the user.
 * 
 * @author TomTzook
 * @since FlashLib 1.0.1
 */
public class PeriodicRunnable implements Runnable{

	private Runnable runnable;
	private int time;
	private int lastRun = -1;
	
	public PeriodicRunnable(Runnable run, int ms){
		this.runnable = run;
		this.time = ms;
	}
	public PeriodicRunnable(Runnable run, double sec){
		this(run, (int)(sec * 0.001));
	}
	
	@Override
	public void run() {
		if(lastRun < 0 || FlashUtil.millisInt() - lastRun >= time){
			runnable.run();
			lastRun = FlashUtil.millisInt();
		}
	}
}
