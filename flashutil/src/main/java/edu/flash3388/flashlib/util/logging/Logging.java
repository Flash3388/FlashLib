package edu.flash3388.flashlib.util.logging;

import edu.flash3388.flashlib.util.logging.jul.JsonFormatter;
import edu.flash3388.flashlib.util.logging.jul.JulLoggerAdapter;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;

public class Logging {

    private Logging() {}

    public static Logger getInnerLogger(String name) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
        logger.addHandler(new ConsoleHandler());

        return new JulLoggerAdapter(logger);
    }

    public static Logger getLogger(File parentDirectory, String name) throws IOException {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);

        String directoryPath = getLoggerFileParentPath(parentDirectory, name);
        Files.createDirectories(Paths.get(directoryPath));

        String filePattern = directoryPath + getLoggerFileNamePattern();

        FileHandler fileHandler = new FileHandler(filePattern);
        fileHandler.setFormatter(new JsonFormatter(false));
        logger.addHandler(fileHandler);

        return new JulLoggerAdapter(logger);
    }

    private static String getLoggerFileParentPath(File parentDirectory, String name) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");

        return String.format("%s/logs/%s/%s/", parentDirectory.getAbsolutePath(), name, dateFormat.format(new Date()));
    }

    private static String getLoggerFileNamePattern() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh_mm_ss");

        return String.format("log_%s.%%g.log", dateFormat.format(new Date()));
    }
}
