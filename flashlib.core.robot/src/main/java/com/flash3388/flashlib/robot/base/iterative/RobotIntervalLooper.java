package com.flash3388.flashlib.robot.base.iterative;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.concurrent.Sleeper;

public class RobotIntervalLooper implements RobotLooper {

	private static final Time DEFAULT_ITERATION_INTERVAL = Time.milliseconds(20);

	private final Sleeper mSleeper;
	private final Time mIterationInterval;
	private final BooleanProperty mRunLoopProperty;

	public RobotIntervalLooper(Sleeper sleeper, Time iterationInterval) {
        mSleeper = sleeper;
        mIterationInterval = iterationInterval;
        mRunLoopProperty = new SimpleBooleanProperty(true);
    }

    public RobotIntervalLooper() {
        this(new Sleeper(), DEFAULT_ITERATION_INTERVAL);
    }

    @Override
	public void startLooping(Clock clock, Runnable loopTask) {
        while(mRunLoopProperty.getAsBoolean()){
            Time start = clock.currentTime();
            loopTask.run();

            Time delay = mIterationInterval.sub(clock.currentTime().sub(start));

            if (delay.isValid()) {
                try {
                    mSleeper.sleepWhileConditionMet(mRunLoopProperty, delay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
	}

	@Override
	public void stop() {
        mRunLoopProperty.setAsBoolean(false);
    }
}
