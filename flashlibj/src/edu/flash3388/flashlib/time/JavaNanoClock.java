package edu.flash3388.flashlib.time;

public class JavaNanoClock implements Clock {

    private final long mStartTimeNanos;

    public JavaNanoClock() {
        mStartTimeNanos = System.nanoTime();
    }

    @Override
    public long currentTimeMillis() {
        return (long) ((System.nanoTime() - mStartTimeNanos) * 1e-6);
    }
}
