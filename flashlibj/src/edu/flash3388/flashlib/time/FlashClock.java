package edu.flash3388.flashlib.time;

import java.util.concurrent.atomic.AtomicReference;

public class FlashClock {

    private static final AtomicReference<Clock> mClockReference =
            new AtomicReference<>(new JavaNanoClock());

    public static Clock getClock() {
        return mClockReference.get();
    }

    public static void setClock(Clock clock) {
        mClockReference.set(clock);
    }
}
