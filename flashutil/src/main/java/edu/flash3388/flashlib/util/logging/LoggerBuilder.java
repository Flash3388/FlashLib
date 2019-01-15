package edu.flash3388.flashlib.util.logging;

import edu.flash3388.flashlib.util.logging.jul.JsonFormatter;
import edu.flash3388.flashlib.util.logging.jul.JulLoggerAdapter;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;

public class LoggerBuilder {

    private final String mName;

    private boolean mEnableFileLogging;
    private String mFilePattern;
    private File mLogsParent;
    private Formatter mFileHandlerFormatter;

    private boolean mEnableConsoleLogging;

    private LogLevel mLogLevel;

    public LoggerBuilder(String name) {
        mName = name;

        mEnableFileLogging = false;
        mFilePattern = "";
        mLogsParent = null;
        mFileHandlerFormatter = new JsonFormatter();

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
        mFilePattern = filePattern;
        return this;
    }

    public LoggerBuilder setTimeBasedFilePattern() {
        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh_mm_ss");
        String filePattern = String.format("log_%s.%%g.log", dateFormat.format(date));

        return setFilePattern(filePattern);
    }

    public LoggerBuilder setLogFilesParent(File parent) {
        mLogsParent = parent;
        return this;
    }

    public LoggerBuilder setDateBasedFilesParent(File parent) {
        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String directoryPath = String.format("%s/logs/%s/%s", parent.getAbsolutePath(), mName, dateFormat.format(date));

        return setLogFilesParent(new File(directoryPath));
    }

    public LoggerBuilder setFileLogFormatter(Formatter formatter) {
        mFileHandlerFormatter = formatter;
        return this;
    }

    public LoggerBuilder setLogLevel(LogLevel logLevel) {
        mLogLevel = logLevel;
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

                FileHandler fileHandler = new FileHandler(pattern);
                fileHandler.setFormatter(mFileHandlerFormatter);
                fileHandler.setLevel(mLogLevel.getJulLevel());

                logger.addHandler(fileHandler);
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
}
