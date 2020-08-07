package com.flash3388.flashlib.util.resources;

import java.io.Closeable;

public final class Resources {

    private Resources() {}

    public static Resource fromCloseable(AutoCloseable closeable) {
        return () -> {
            try {
                closeable.close();
            } catch (Exception e) {}
        };
    }

    public static Closeable toCloseable(Resource resource) {
        return resource::free;
    }

    public static AutoCloseable toAutoCloseable(Resource resource) {
        return resource::free;
    }
}
