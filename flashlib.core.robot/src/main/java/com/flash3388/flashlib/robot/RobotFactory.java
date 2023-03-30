package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.impl.SingleThreadedScheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.util.FlashLibMainThread;

public final class RobotFactory {

    private RobotFactory() {}

    public static Scheduler newDefaultScheduler(Clock clock) {
        return new SingleThreadedScheduler(clock, new StoredObject.Stub(), new FlashLibMainThread.Stub());
    }

    public static Scheduler newDefaultScheduler(Clock clock,
                                                ObjectStorage objectStorage,
                                                FlashLibMainThread mainThread) {
        StoredObject object = objectStorage.getInstanceRoot().getChild("FlashLib").getChild("Scheduler");
        return new SingleThreadedScheduler(clock, object, mainThread);
    }

    public static Clock newDefaultClock() {
        return new SystemNanoClock();
    }
}
