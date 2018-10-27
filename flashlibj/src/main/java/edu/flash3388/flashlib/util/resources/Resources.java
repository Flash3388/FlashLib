package edu.flash3388.flashlib.util.resources;

import edu.flash3388.flashlib.io.Closeables;

import java.io.Closeable;

public class Resources {

    private Resources() {}

    public static Resource fromCloseable(Closeable closeable) {
        return () -> Closeables.closeQuietly(closeable);
    }

    public static Closeable toCloseable(Resource resource) {
        return resource::free;
    }

    public static Resource fromAutoCloseable(AutoCloseable autoCloseable) {
        return () -> Closeables.closeQuietly(autoCloseable);
    }

    public static AutoCloseable toAutoCloseable(Resource resource) {
        return resource::free;
    }
}
