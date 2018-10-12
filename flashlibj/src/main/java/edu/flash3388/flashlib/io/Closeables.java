package edu.flash3388.flashlib.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Closeables {
    private Closeables() {}

    private static final Logger LOGGER = Logger.getLogger(Closeables.class.getName());

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "error while closing closeable quietly", e);
        }
    }
}
