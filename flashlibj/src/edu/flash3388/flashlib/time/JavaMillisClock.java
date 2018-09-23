package edu.flash3388.flashlib.time;

public class JavaMillisClock implements Clock {

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
