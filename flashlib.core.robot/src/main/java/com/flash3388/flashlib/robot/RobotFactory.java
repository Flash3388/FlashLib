package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.app.net.NetworkInterfaceImpl;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.impl.SingleThreadedScheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;

public final class RobotFactory {

    private RobotFactory() {}

    public static Scheduler newDefaultScheduler(Clock clock) {
        return new SingleThreadedScheduler(clock, new StoredObject.Stub());
    }

    public static Scheduler newDefaultScheduler(Clock clock, ObjectStorage objectStorage) {
        StoredObject object = objectStorage.getRoot().getChild("FlashLib").getChild("Scheduler");
        return new SingleThreadedScheduler(clock, object);
    }

    public static Clock newDefaultClock() {
        return new SystemNanoClock();
    }

    public static NetworkInterface disabledNetworkInterface() {
        return new NetworkInterfaceImpl();
    }
}
