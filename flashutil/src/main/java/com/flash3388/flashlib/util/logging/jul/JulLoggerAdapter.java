package com.flash3388.flashlib.util.logging.jul;

import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JulLoggerAdapter extends MarkerIgnoringBase implements LocationAwareLogger {

    private static String SUPER = MarkerIgnoringBase.class.getName();
    private static final String SELF = JulLoggerAdapter.class.getName();

    private final java.util.logging.Logger mLogger;

    public JulLoggerAdapter(java.util.logging.Logger logger) {
        mLogger = logger;
    }

    @Override
    public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable t) {
        Level julLevel = slf4jLevelIntToJulLevel(level);

        if (mLogger.isLoggable(julLevel)) {
            log(fqcn, julLevel, message, t);
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return mLogger.isLoggable(Level.FINEST);
    }

    @Override
    public void trace(String msg) {
        if (isTraceEnabled()) {
            log(SELF, Level.FINEST, msg, null);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            logArgs(SELF, Level.FINEST, format, new Object[]{arg});
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            logArgs(SELF, Level.FINEST, format, new Object[]{arg1, arg2});
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            logArgs(SELF, Level.FINEST, format, arguments);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            log(SELF, Level.FINEST, msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return mLogger.isLoggable(Level.FINE);
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled()) {
            log(SELF, Level.FINE, msg, null);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            logArgs(SELF, Level.FINE, format, new Object[]{arg});
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            logArgs(SELF, Level.FINE, format, new Object[]{arg1, arg2});
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            logArgs(SELF, Level.FINE, format, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            log(SELF, Level.FINE, msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return mLogger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String msg) {
        if (isInfoEnabled()) {
            log(SELF, Level.INFO, msg, null);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            logArgs(SELF, Level.INFO, format, new Object[]{arg});
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            logArgs(SELF, Level.INFO, format, new Object[]{arg1, arg2});
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            logArgs(SELF, Level.INFO, format, arguments);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            log(SELF, Level.INFO, msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return mLogger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String msg) {
        if (isWarnEnabled()) {
            log(SELF, Level.WARNING, msg, null);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            logArgs(SELF, Level.WARNING, format, new Object[]{arg});
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            logArgs(SELF, Level.WARNING, format, new Object[]{arg1, arg2});
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            logArgs(SELF, Level.WARNING, format, arguments);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            log(SELF, Level.WARNING, msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return mLogger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String msg) {
        if (isErrorEnabled()) {
            log(SELF, Level.SEVERE, msg, null);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            logArgs(SELF, Level.SEVERE, format, new Object[]{arg});
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            logArgs(SELF, Level.SEVERE, format, new Object[]{arg1, arg2});
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            logArgs(SELF, Level.SEVERE, format, arguments);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            log(SELF, Level.SEVERE, msg, t);
        }
    }

    private void logArgs(String callerFQCN, Level level, String format, Object[] args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, args);

        LogRecord record = createLogRecord(callerFQCN, level, ft.getMessage(), ft.getThrowable());
        record.setParameters(ft.getArgArray());

        mLogger.log(record);
    }

    private void log(String callerFQCN, Level level, String msg, Throwable t) {
        LogRecord record = createLogRecord(callerFQCN, level, msg, t);
        mLogger.log(record);
    }

    private LogRecord createLogRecord(String callerFQCN, Level level, String msg, Throwable t) {
        LogRecord record = new LogRecord(level, msg);
        record.setLoggerName(getName());
        record.setThrown(t);

        fillCallerData(callerFQCN, record);

        return record;
    }

    private void fillCallerData(String callerFQCN, LogRecord record) {
        StackTraceElement[] steArray = new Throwable().getStackTrace();

        int selfIndex = -1;
        for (int i = 0; i < steArray.length; i++) {
            final String className = steArray[i].getClassName();
            if (className.equals(callerFQCN) || className.equals(SUPER)) {
                selfIndex = i;
                break;
            }
        }

        int found = -1;
        for (int i = selfIndex + 1; i < steArray.length; i++) {
            final String className = steArray[i].getClassName();
            if (!(className.equals(callerFQCN) || className.equals(SUPER))) {
                found = i;
                break;
            }
        }

        if (found != -1) {
            StackTraceElement ste = steArray[found];
            // setting the class name has the side effect of setting
            // the needToInferCaller variable to false.
            record.setSourceClassName(ste.getClassName());
            record.setSourceMethodName(ste.getMethodName());
        }
    }

    private Level slf4jLevelIntToJulLevel(int slf4jLevelInt) {
        Level julLevel;
        switch (slf4jLevelInt) {
            case LocationAwareLogger.TRACE_INT:
                julLevel = Level.FINEST;
                break;
            case LocationAwareLogger.DEBUG_INT:
                julLevel = Level.FINE;
                break;
            case LocationAwareLogger.INFO_INT:
                julLevel = Level.INFO;
                break;
            case LocationAwareLogger.WARN_INT:
                julLevel = Level.WARNING;
                break;
            case LocationAwareLogger.ERROR_INT:
                julLevel = Level.SEVERE;
                break;
            default:
                throw new IllegalStateException("Level number " + slf4jLevelInt + " is not recognized.");
        }
        return julLevel;
    }
}
