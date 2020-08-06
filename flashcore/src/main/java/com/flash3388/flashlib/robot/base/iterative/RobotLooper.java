package com.flash3388.flashlib.robot.base.iterative;

import com.flash3388.flashlib.time.Clock;

public interface RobotLooper {

    void doLoop(Clock clock, Runnable loopTask);
    void stopLoop();
}
