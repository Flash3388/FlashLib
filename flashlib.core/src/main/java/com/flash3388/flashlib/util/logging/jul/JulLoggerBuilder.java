package com.flash3388.flashlib.util.logging.jul;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class JulLoggerBuilder {

    private final String mName;
    private final Collection<Handler> mHandlers;

    public JulLoggerBuilder(String name) {
        mName = name;

        mHandlers = new ArrayList<>();
    }

    public JulLoggerBuilder addHandler(Handler handler) {
        mHandlers.add(handler);
        return this;
    }

    public Logger build() {
        Logger logger = Logger.getLogger(mName);
        logger.setUseParentHandlers(false);

        for (Handler handler : mHandlers) {
            logger.addHandler(handler);
        }

        return logger;
    }
}
