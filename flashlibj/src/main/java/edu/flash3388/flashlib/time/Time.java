package edu.flash3388.flashlib.time;

public class Time {
    private Time() {}

    public static final long INVALID_TIME = -1L;

    public static double millisToSeconds(long millis) {
        return millis * 1e-3;
    }

    public static long secondsToMillis(double seconds) {
        return (long) (seconds * 1e3);
    }
}
