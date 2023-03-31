package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.impl.SchedulerImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.util.FlashLibMainThread;

public final class RobotFactory {

    private RobotFactory() {}

    public static Scheduler newDefaultScheduler(Clock clock) {
        return new SchedulerImpl(new FlashLibMainThread.Stub(), clock, new StoredObject.Stub());
    }

    public static Scheduler newDefaultScheduler(Clock clock,
                                                ObjectStorage objectStorage,
                                                FlashLibMainThread mainThread) {
        StoredObject object = objectStorage.getInstanceRoot().getChild("FlashLib").getChild("Scheduler");
        return new SchedulerImpl(mainThread, clock, object);
    }

    public static Clock newDefaultClock() {
        return new SystemNanoClock();
    }
}
