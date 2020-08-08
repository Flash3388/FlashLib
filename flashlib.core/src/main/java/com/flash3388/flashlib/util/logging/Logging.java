package com.flash3388.flashlib.util.logging;

import com.flash3388.flashlib.util.logging.jul.JsonFormatter;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public final class Logging {

    private Logging() {}

    public static Logger consoleLogger(String name) {
        return new LoggerBuilder(name)
                .enableConsoleLogging(true)
                .build();
    }

    public static Logger fileLogger(Path parent, String name) throws IOException {
        return new LoggerBuilder(name)
                .enableFileLogging(true)
                .setFileLogFormatter(new JsonFormatter())
                .setDateBasedFilesParent(parent)
                .setTimeBasedFilePattern()
                .build();
    }

    public static Logger stub() {
        return new StubLogger();
    }
}
