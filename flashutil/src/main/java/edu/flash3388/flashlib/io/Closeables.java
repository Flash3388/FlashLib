package edu.flash3388.flashlib.io;

import edu.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;

public class Closeables {
    private Closeables() {}

    private static final Logger LOGGER = Logging.getConsoleLogger(Closeables.class.getName());

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            LOGGER.warn("error while closing closeable quietly", e);
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            LOGGER.warn("error while closing auto closeable quietly", e);
        }
    }
}
