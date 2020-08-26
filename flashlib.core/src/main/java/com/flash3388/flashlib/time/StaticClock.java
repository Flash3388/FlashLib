package com.flash3388.flashlib.time;

public class StaticClock implements Clock {

    private final Time mTime;

    public StaticClock(Time time) {
        mTime = time;
    }

    public StaticClock() {
        this(Time.INVALID);
    }

    @Override
    public Time currentTime() {
        return mTime;
    }
}
