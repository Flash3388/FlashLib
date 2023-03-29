package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.impl.SingleThreadedScheduler;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.util.FlashLibMainThread;

import java.util.function.Supplier;

public enum SchedulerImpl {
    NEW_SINGLE_THREAD(()->new SingleThreadedScheduler(
            new SystemNanoClock(),
            new StoredObject.Stub(),
            new FlashLibMainThread.Stub()))
    ;

    private final Supplier<Scheduler> mGenerator;

    SchedulerImpl(Supplier<Scheduler> generator) {
        mGenerator = generator;
    }

    public Scheduler create() {
        return mGenerator.get();
    }
}
