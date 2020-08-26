package com.flash3388.flashlib.util.logging;

import java.util.logging.Level;

public enum LogLevel {
    TRACE(Level.FINEST),
    DEBUG(Level.FINE),
    INFO(Level.INFO),
    WARN(Level.WARNING),
    ERROR(Level.SEVERE);

    private final Level mJulLevel;

    LogLevel(Level julLevel) {
        mJulLevel = julLevel;
    }

    public Level getJulLevel() {
        return mJulLevel;
    }
}
