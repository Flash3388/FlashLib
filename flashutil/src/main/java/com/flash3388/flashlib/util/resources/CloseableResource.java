package com.flash3388.flashlib.util.resources;

import org.slf4j.Logger;

public class CloseableResource implements Resource {

    private final AutoCloseable mCloseable;
    private final Logger mLogger;

    public CloseableResource(AutoCloseable closeable, Logger logger) {
        mCloseable = closeable;
        mLogger = logger;
    }

    @Override
    public void free() {
        try {
            mCloseable.close();
        } catch (Throwable t) {
            mLogger.warn("Error while closing closeable", t);
        }
    }
}
