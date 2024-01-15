package com.flash3388.flashlib.robot.base.iterative;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public interface RobotLooper {

    Time getLoopRunPeriod();

    void startLooping(Clock clock, Runnable loopTask);
    void stop();
}
