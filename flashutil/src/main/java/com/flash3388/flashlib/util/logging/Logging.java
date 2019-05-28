package com.flash3388.flashlib.util.logging;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class Logging {

    private Logging() {}

    public static Logger getConsoleLogger(String name) {
        return new LoggerBuilder(name)
                .enableConsoleLogging(true)
                .build();
    }

    public static Logger getLogger(Path parent, String name) throws IOException {
        return new LoggerBuilder(name)
                .enableFileLogging(true)
                .setDateBasedFilesParent(parent)
                .setTimeBasedFilePattern()
                .build();
    }
}
