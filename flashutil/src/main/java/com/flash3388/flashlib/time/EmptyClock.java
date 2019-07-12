package com.flash3388.flashlib.time;

public class EmptyClock implements Clock {

    private final Time mTime;

    public EmptyClock(Time time) {
        mTime = time;
    }

    public EmptyClock() {
        this(Time.INVALID);
    }

    @Override
    public Time currentTime() {
        return mTime;
    }
}
