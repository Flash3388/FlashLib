package edu.flash3388.flashlib.robot.scheduling;

import java.util.concurrent.atomic.AtomicReference;

public class FlashScheduler {

    private static final AtomicReference<Scheduler> mSchedulerReference =
            new AtomicReference<>(new Scheduler());

    public static Scheduler get() {
        return mSchedulerReference.get();
    }

    public static void set(Scheduler scheduler) {
        mSchedulerReference.set(scheduler);
    }
}
