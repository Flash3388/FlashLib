package edu.flash3388.flashlib.util.logging;

import edu.flash3388.flashlib.util.logging.jul.DelegatingHandler;
import edu.flash3388.flashlib.util.logging.jul.FlusherThreadFactory;
import edu.flash3388.flashlib.util.logging.jul.JsonFormatter;
import edu.flash3388.flashlib.util.logging.jul.JulLoggerAdapter;
import edu.flash3388.flashlib.util.logging.jul.LogFlushingTask;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;

public class LoggerBuilder {

    private static final int DEFAULT_FILE_SIZE_BYTES = 1048576; // 1 MB
    private static final int DEFAULT_FILE_COUNT = 10;
    private static final int DEFAULT_DELEGATED_LOGGING_CAPACITY = 1024;

    private final String mName;

    private boolean mEnableFileLogging;
    private String mFilePattern;
    private File mLogsParent;
    private Formatter mFileHandlerFormatter;
    private LogFileConfig mLogFileConfig;

    private boolean mEnableDelegatedFileLogging;
    private int mDelegatedLoggingCapacity;
    private ThreadFactory mFlushingThreadFactory;

    private boolean mEnableConsoleLogging;

    private LogLevel mLogLevel;

    public LoggerBuilder(String name) {
        mName = name;

        mEnableFileLogging = false;
        mFilePattern = "";
        mLogsParent = null;
        mFileHandlerFormatter = new JsonFormatter();
        mLogFileConfig = new LogFileConfig(DEFAULT_FILE_SIZE_BYTES, DEFAULT_FILE_COUNT);

        mEnableDelegatedFileLogging = false;
        mDelegatedLoggingCapacity = DEFAULT_DELEGATED_LOGGING_CAPACITY;
        mFlushingThreadFactory = new FlusherThreadFactory(name);

        mEnableConsoleLogging = false;

        mLogLevel = LogLevel.INFO;
    }

    public LoggerBuilder enableConsoleLogging(boolean enable) {
        mEnableConsoleLogging = enable;
        return this;
    }

    public LoggerBuilder enableFileLogging(boolean enable) {
        mEnableFileLogging = enable;
        return this;
    }

    public LoggerBuilder setFilePattern(String filePattern) {
        mFilePattern = Objects.requireNonNull(filePattern);
        return this;
    }

    public LoggerBuilder setTimeBasedFilePattern() {
        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh_mm_ss");
        String filePattern = String.format("log_%s.%%g.log", dateFormat.format(date));

        return setFilePattern(filePattern);
    }

    public LoggerBuilder setLogFilesParent(File parent) {
        mLogsParent = Objects.requireNonNull(parent);
        return this;
    }

    public LoggerBuilder setDateBasedFilesParent(File parent) {
        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String directoryPath = String.format("%s/logs/%s/%s", parent.getAbsolutePath(), mName, dateFormat.format(date));

        return setLogFilesParent(new File(directoryPath));
    }

    public LoggerBuilder setFileLogFormatter(Formatter formatter) {
        mFileHandlerFormatter = Objects.requireNonNull(formatter);
        return this;
    }

    public LoggerBuilder setLogFileConfig(LogFileConfig logFileConfig) {
        mLogFileConfig = Objects.requireNonNull(logFileConfig);
        return this;
    }

    public LoggerBuilder enableDelegatedFileLogging(boolean delegatedFileLogging) {
        mEnableDelegatedFileLogging = delegatedFileLogging;
        return this;
    }

    public LoggerBuilder setDelegatedLoggingCapacity(int delegatedLoggingCapacity) {
        if (delegatedLoggingCapacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }

        mDelegatedLoggingCapacity = delegatedLoggingCapacity;
        return this;
    }

    public LoggerBuilder setFlushingThreadFactory(ThreadFactory threadFactory) {
        mFlushingThreadFactory = Objects.requireNonNull(threadFactory);
        return this;
    }

    public LoggerBuilder setLogLevel(LogLevel logLevel) {
        mLogLevel = Objects.requireNonNull(logLevel);
        return this;
    }

    public java.util.logging.Logger buildJul() {
        try {
            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(mName);
            logger.setUseParentHandlers(false);

            if (mEnableConsoleLogging) {
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(mLogLevel.getJulLevel());

                logger.addHandler(consoleHandler);
            }

            if (mEnableFileLogging && mFilePattern != null && mFilePattern.length() > 0) {
                String pattern;

                if (mLogsParent != null) {
                    pattern = mLogsParent.getAbsolutePath().concat(File.separator).concat(mFilePattern);
                    Files.createDirectories(mLogsParent.toPath());
                } else {
                    pattern = mFilePattern;
                }

                FileHandler fileHandler = new FileHandler(pattern,
                        mLogFileConfig.getSizeLimitBytes(), mLogFileConfig.getFileCount());
                fileHandler.setFormatter(mFileHandlerFormatter);
                fileHandler.setLevel(mLogLevel.getJulLevel());

                if (mEnableDelegatedFileLogging) {
                    Handler delegatedHandler = new DelegatingHandler(fileHandler, mDelegatedLoggingCapacity);
                    delegatedHandler.setLevel(mLogLevel.getJulLevel());

                    startLogFlusher(delegatedHandler);
                    logger.addHandler(delegatedHandler);
                } else {
                    logger.addHandler(fileHandler);
                }
            }

            logger.setLevel(mLogLevel.getJulLevel());

            return logger;
        } catch (IOException e) {
            throw new LogBuildException(e);
        }
    }

    public Logger build() {
        return new JulLoggerAdapter(buildJul());
    }

    private void startLogFlusher(Handler handler) {
        Thread thread = mFlushingThreadFactory.newThread(new LogFlushingTask(handler));
        thread.start();
    }
}
