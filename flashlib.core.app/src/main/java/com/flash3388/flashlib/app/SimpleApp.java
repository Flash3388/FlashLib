package com.flash3388.flashlib.app;

public interface SimpleApp extends AutoCloseable {

    interface Creator {
        SimpleApp create(FlashLibControl control) throws StartupException;
    }

    void main() throws Exception;
}
