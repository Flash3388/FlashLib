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

public class Logging {

    private Logging() {}

    public static Logger getConsoleLogger(String name) {
        return new JulLoggerAdapter(getJulConsoleLogger(name));
    }

    public static java.util.logging.Logger getJulConsoleLogger(String name) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
        logger.addHandler(new ConsoleHandler());

        return logger;
    }

    public static Logger getLogger(File parentDirectory, String name) throws IOException {
        return new JulLoggerAdapter(getJulLogger(parentDirectory, name));
    }

    public static java.util.logging.Logger getJulLogger(File parentDirectory, String name) throws IOException {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);

        File parent = formatLogDirectory(parentDirectory, name);
        Files.createDirectories(parent.toPath());

        FileHandler fileHandler = new FileHandler(String.format("%s/%s",
                parent.getAbsolutePath(),
                getLoggerFileNamePattern()));

        fileHandler.setFormatter(new JsonFormatter(false));
        logger.addHandler(fileHandler);

        return logger;
    }

    public static File formatLogDirectory(File parent, String loggerName) {
        return new File(getLoggerFileParentPath(parent, loggerName));
    }

    private static String getLoggerFileParentPath(File parentDirectory, String name) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");

        return String.format("%s/logs/%s/%s", parentDirectory.getAbsolutePath(), name, dateFormat.format(new Date()));
    }

    private static String getLoggerFileNamePattern() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh_mm_ss");

        return String.format("log_%s.%%g.log", dateFormat.format(new Date()));
    }
}
