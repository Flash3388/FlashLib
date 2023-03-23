package com.flash3388.flashlib.util.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Logging {

    private Logging() {}

    public static Logger stub() {
        return new StubLogger();
    }

    public static Logger getLogger(String first, String... more) {
        return LoggerFactory.getLogger(constructName(first, more));
    }

    public static Logger getMainLogger() {
        return getLogger("Main");
    }

    private static String constructName(String first, String... more) {
        StringBuilder builder = new StringBuilder();
        builder.append("FlashLib");
        builder.append('.');
        builder.append(first);

        for (int i = 0; i < more.length; i++) {
            builder.append('.');
            builder.append(more[i]);
        }

        return builder.toString();
    }
}
