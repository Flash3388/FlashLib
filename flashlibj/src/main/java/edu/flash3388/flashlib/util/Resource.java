package edu.flash3388.flashlib.util;

@FunctionalInterface
public interface Resource extends AutoCloseable {

    void free();

    @Override
    default void close() {
        free();
    }
}
