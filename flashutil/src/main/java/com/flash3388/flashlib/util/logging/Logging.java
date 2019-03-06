package com.flash3388.flashlib.util.logging;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public class Logging {

    private Logging() {}

    public static Logger getConsoleLogger(String name) {
        return new LoggerBuilder(name)
                .enableConsoleLogging(true)
                .build();
    }

    public static Logger getLogger(File parent, String name) throws IOException {
        return new LoggerBuilder(name)
                .enableFileLogging(true)
                .setDateBasedFilesParent(parent)
                .setTimeBasedFilePattern()
                .build();
    }
}
